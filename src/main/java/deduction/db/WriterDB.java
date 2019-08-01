package deduction.db;


import deduction.Writer;
import deduction.db.dto.ExpressionsDTO;
import deduction.db.dto.KnownFactsDTO;
import deduction.db.dto.RulesDTO;

import deduction.db.mappers.ExpressionsMapper;
import deduction.db.mappers.KnownFactsMapper;
import deduction.db.mappers.ModelMapper;
import deduction.db.mappers.RulesMapper;
import deduction.model.Model;
import deduction.model.Rule;
import deduction.model.expression.Expression;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Iterator;
import java.util.List;


public class WriterDB implements Writer {

    private SqlSessionFactory ssf;
    private int elementNumber;

    public WriterDB(SqlSessionFactory ssf) {
        this.ssf = ssf;
    }

    @Override
    public void convert(String modelName, Model model) {

        try (SqlSession session = ssf.openSession()) {

            ModelMapper modelMapper = session.getMapper(ModelMapper.class);
            modelMapper.insertNameInModel(modelName);
            session.commit();

            int modelId = modelMapper.getModelId(modelName);

            KnownFactsMapper knownFactsMapper = session.getMapper(KnownFactsMapper.class);
            for (String fact : model.getKnownFactsList()) {
                knownFactsMapper.insertKnownFacts(new KnownFactsDTO(modelId, fact));
                session.commit();
            }

            RulesMapper rulesDBMapper = session.getMapper(RulesMapper.class);
            for (Rule rule : model.getRulesList()) {

                RulesDTO rulesDTO = new RulesDTO(rule.getResultFact(), modelId);
                rulesDBMapper.insertRulesDB(rulesDTO);
                session.commit();

                Expression expression = rule.getExpression();
                if (rule.getExpression().getStringPresentation().equals("Fact")) {
                    ExpressionsMapper expressionMapper = session.getMapper(ExpressionsMapper.class);
//PRINT
                    //   System.out.println(expression.toString());
                    expressionMapper.insertElement(new ExpressionsDTO(rulesDTO.id, 1, null, expression.toString(), "fact"));
                    session.commit();
                } else {
                    elementNumber = 0;
                    serializeExpression(session, expression, rulesDTO.id, null);
                }
            }
        }
    }


    private void serializeExpression(SqlSession session, Expression ex, int id_, Integer parentId_) {
        ExpressionsMapper ruleDBMapper = session.getMapper(ExpressionsMapper.class);
        Integer parentId = parentId_;

        if (ex.getStringPresentation().equals("Fact")) {
            ruleDBMapper.insertElement(new ExpressionsDTO(id_, ++elementNumber, parentId, ex.toString(), "fact"));
            session.commit();
        }
        if (ex.getStringPresentation().equals("Or")) {
            ruleDBMapper.insertElement(new ExpressionsDTO(id_, ++elementNumber, parentId, null, "or"));
            session.commit();
        }
        if (ex.getStringPresentation().equals("And")) {
            ruleDBMapper.insertElement(new ExpressionsDTO(id_, ++elementNumber, parentId, null, "and"));
            session.commit();
        }

        parentId = elementNumber;
        for (Iterator<Expression> iterator = ex.getExpressions().iterator(); iterator.hasNext(); ) {
            Expression expression = iterator.next();
            if (!(expression.getStringPresentation().equals("Fact"))) {
                serializeExpression(session, expression, id_, parentId);
                continue;
            } else {
                ruleDBMapper.insertElement(new ExpressionsDTO(id_, ++elementNumber, parentId, expression.toString(), "fact"));
                session.commit();
            }
            if (!iterator.hasNext()) {
                break;
            }
        }
    }


    public void deleteModelDB(String modelName) {
        try (SqlSession session = ssf.openSession()) {

            RulesMapper rulesMapper = session.getMapper(RulesMapper.class);
            List<RulesDTO> rulesDTOList = rulesMapper.getRules(modelName);

            ExpressionsMapper expressionDTOMapper = session.getMapper(ExpressionsMapper.class);
            for (RulesDTO ruleDB : rulesDTOList) {

                expressionDTOMapper.deleteExpression(ruleDB.id);
                session.commit();
            }
            for (RulesDTO ruleDB : rulesDTOList) {
                rulesMapper.deleteRules(ruleDB.id);
                session.commit();
            }
            KnownFactsMapper factsMapper = session.getMapper(KnownFactsMapper.class);
            factsMapper.deleteKnownFacts(modelName);
            session.commit();

            ModelMapper modelMapper = session.getMapper(ModelMapper.class);
            modelMapper.deleteModel(modelName);
            session.commit();
        }
        System.out.println("Delete model: " + "'" + modelName + "'" + " is successfully");
    }
}