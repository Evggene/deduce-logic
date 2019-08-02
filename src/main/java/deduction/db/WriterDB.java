package deduction.db;


import deduction.Writer;
import deduction.db.dto.ExpressionsDTO;
import deduction.db.dto.KnownFactsDTO;
import deduction.db.dto.ModelDTO;
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
    private int elementId;

    public WriterDB(SqlSessionFactory ssf) {
        this.ssf = ssf;
    }

    @Override
    public void convert(String modelName, Model model) {

        try (SqlSession session = ssf.openSession()) {

            ModelMapper modelMapper = session.getMapper(ModelMapper.class);
            ModelDTO modelDTO = new ModelDTO(modelName);
            modelMapper.insertModel(modelDTO);
            session.commit();

            int modelId = modelDTO.id;

            KnownFactsMapper knownFactsMapper = session.getMapper(KnownFactsMapper.class);
            for (String fact : model.getKnownFactsList()) {
                knownFactsMapper.insertKnownFacts(new KnownFactsDTO(modelId, fact));
                session.commit();
            }

            RulesMapper rulesDBMapper = session.getMapper(RulesMapper.class);
            for (Rule rule : model.getRulesList()) {

                Expression expression = rule.getExpression();
                ExpressionsMapper expressionMapper = session.getMapper(ExpressionsMapper.class);
                ExpressionsDTO expressionsDTO = null;

                if (expression.getStringPresentation().equals("Fact")) {
                    expressionsDTO = new ExpressionsDTO(null, expression.toString(), "fact");
                    expressionMapper.insertElement(expressionsDTO);
                    session.commit();
                } else {
                    if (expression.getStringPresentation().equals("And")) {
                        expressionsDTO = new ExpressionsDTO(null, null, "and");
                    }
                    if (expression.getStringPresentation().equals("Or")) {
                        expressionsDTO = new ExpressionsDTO(null, null, "or");
                    }
                    expressionMapper.insertElement(expressionsDTO);
                    session.commit();
                    serializeExpression(session, expression, expressionsDTO.id);
                }
                RulesDTO rulesDTO = new RulesDTO(rule.getResultFact(), modelId, expressionsDTO.id);
                rulesDBMapper.insertRulesDB(rulesDTO);
                session.commit();
            }
        }
    }


    private void serializeExpression(SqlSession session, Expression expression, Integer previousParentId) {
        ExpressionsMapper expressionMapper = session.getMapper(ExpressionsMapper.class);
        Integer currentParentId;

        if (expression.getStringPresentation().equals("Fact")) {
            ExpressionsDTO expressionsDTO = new ExpressionsDTO(previousParentId, expression.toString(), "fact");
            expressionMapper.insertElement(expressionsDTO);
            session.commit();
        }

        for (Iterator<Expression> iterator = expression.getExpressions().iterator(); iterator.hasNext(); ) {
            Expression subExpression = iterator.next();
            if (!(subExpression.getStringPresentation().equals("Fact"))) {
                ExpressionsDTO expressionsDTO = null;
                if (subExpression.getStringPresentation().equals("Or")) {
                    expressionsDTO = new ExpressionsDTO(previousParentId, null, "or");
                }
                if (subExpression.getStringPresentation().equals("And")) {
                    expressionsDTO = new ExpressionsDTO(previousParentId, null, "and");
                }
                expressionMapper.insertElement(expressionsDTO);
                session.commit();
                currentParentId = expressionsDTO.id;
                serializeExpression(session, subExpression, currentParentId);
            } else {
                expressionMapper.insertElement(new ExpressionsDTO(previousParentId, subExpression.toString(), "fact"));
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