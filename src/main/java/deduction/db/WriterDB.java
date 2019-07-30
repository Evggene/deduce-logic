package deduction.db;


import deduction.Writer;
import deduction.dto.KnownFactsDTOMapper;
import deduction.dto.ModelDTOMapper;
import deduction.dto.ExpressionDTOMapper;
import deduction.dto.RulesDTOMapper;
import deduction.dto.domains.KnownFactsDTO;
import deduction.dto.domains.ExpressionDTO;
import deduction.dto.domains.RulesDTO;
import deduction.model.Model;
import deduction.model.Rule;
import deduction.model.expression.Expression;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;


public class WriterDB implements Writer {


    private int elementNumber = 0;
    SqlSessionFactory ssf;

    public WriterDB(SqlSessionFactory ssf) {
        this.ssf = ssf;
    }

    @Override
    public void convert(String modelName, Model model)  {

        try (SqlSession session = ssf.openSession()) {

            ModelDTOMapper modelMapper = session.getMapper(ModelDTOMapper.class);
            modelMapper.insertNameInModel(modelName);
            session.commit();

            int modelId = modelMapper.getModelIdByName(modelName);

            KnownFactsDTOMapper knownFactsMapper = session.getMapper(KnownFactsDTOMapper.class);
            for (String fact : model.getKnownFactsList()) {
                knownFactsMapper.insertKnownFacts(new KnownFactsDTO(modelId, fact));
                session.commit();
            }

            RulesDTOMapper rulesDBMapper = session.getMapper(RulesDTOMapper.class);
            for (Rule rule : model.getRulesList()) {

                RulesDTO rulesDTO = new RulesDTO(rule.getResultFact(), modelId);
                rulesDBMapper.insertRulesDB(rulesDTO);
                session.commit();

                serializeExpression(session, rule.getExpression(), rulesDTO.getId());
            }
        }
    }


    private void serializeExpression(SqlSession session, Expression expression, int ruleId) {

        if (expression.getStringPresentation().equals("Fact")) {
            ExpressionDTOMapper ruleDBMapper = session.getMapper(ExpressionDTOMapper.class);
  //PRINT
         //   System.out.println(expression.toString());
            ruleDBMapper.insertElement(new ExpressionDTO(ruleId, 1, null, expression.toString(), 3));
            session.commit();
            return;
        }
        elementNumber = 0;
        serializeExpression(session, expression, ruleId, null);
    }


    private void serializeExpression(SqlSession session, Expression ex, int id_, Integer parentId_) {
        ExpressionDTOMapper ruleDBMapper = session.getMapper(ExpressionDTOMapper.class);
        Integer parentId = parentId_;

        if (ex.getStringPresentation().equals("Fact")) {
            ruleDBMapper.insertElement(new ExpressionDTO(id_, ++elementNumber, parentId, ex.toString(), 3));
            session.commit();
        }
        else if (ex.getStringPresentation().equals("Or")){
            ruleDBMapper.insertElement(new ExpressionDTO(id_, ++elementNumber, parentId, null, 1));
            session.commit();
        }
        else {
            ruleDBMapper.insertElement(new ExpressionDTO(id_, ++elementNumber, parentId, null, 2));
            session.commit();
        }


        session.commit();

        parentId = elementNumber;
        for (Iterator<Expression> iterator = ex.getExpressions().iterator(); iterator.hasNext(); ) {
            Expression expression = iterator.next();
            if (!(expression.getStringPresentation().equals("Fact"))) {
                serializeExpression(session, expression, id_, parentId);
                continue;
            }
            else {
                ruleDBMapper.insertElement(new ExpressionDTO(id_, ++elementNumber, parentId, expression.toString(), 3));
                session.commit();
            }
            if (!iterator.hasNext()) {
                break;
            }
        }
    }

    public void deleteModelDB(String modelName) {
        try (SqlSession session = ssf.openSession()) {

            RulesDTOMapper rulesMapper = session.getMapper(RulesDTOMapper.class);
            List<RulesDTO> rulesDTOList = rulesMapper.getRulesByModelName(modelName);

            ExpressionDTOMapper expressionDTOMapper = session.getMapper(ExpressionDTOMapper.class);
            for (RulesDTO ruleDB : rulesDTOList) {

                expressionDTOMapper.deleteExpression(ruleDB.getId());
                session.commit();
            }
            for (RulesDTO ruleDB : rulesDTOList) {
                rulesMapper.deleteRules(ruleDB.getId());
                session.commit();
            }
            KnownFactsDTOMapper factsMapper = session.getMapper(KnownFactsDTOMapper.class);
            factsMapper.deleteKnownFacts(modelName);
            session.commit();

            ModelDTOMapper modelMapper = session.getMapper(ModelDTOMapper.class);
            modelMapper.deleteModel(modelName);
            session.commit();
        }
    }
}
