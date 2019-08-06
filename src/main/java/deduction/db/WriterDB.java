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
import deduction.model.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Iterator;
import java.util.List;


public class WriterDB implements Writer {

    private SqlSessionFactory ssf;

    public WriterDB(SqlSessionFactory ssf) {
        this.ssf = ssf;
    }


    @Override
    public void write(String modelName, Model model) {

        try (SqlSession session = ssf.openSession(false)) {

            ModelMapper modelMapper = session.getMapper(ModelMapper.class);
            ModelDTO modelDTO = new ModelDTO(modelName);
            modelMapper.insertModel(modelDTO);

            int modelId = modelDTO.id;

            KnownFactsMapper knownFactsMapper = session.getMapper(KnownFactsMapper.class);
            for (String fact : model.getKnownFacts()) {
                knownFactsMapper.insertKnownFacts(new KnownFactsDTO(modelId, fact));
            }

            RulesMapper rulesDBMapper = session.getMapper(RulesMapper.class);
            for (Rule rule : model.getRules()) {
                ExpressionsMapper expressionMapper = session.getMapper(ExpressionsMapper.class);
                Expression expression = rule.getExpression();

                ExpressionsDTO expressionsDTO = (ExpressionsDTO) expression.accept(new DBWrapper(null));
                expressionMapper.insertElement(expressionsDTO);

                serializeExpression(session, expression, expressionsDTO.id);

                RulesDTO rulesDTO = new RulesDTO(rule.getResultFact(), modelId, expressionsDTO.id);
                rulesDBMapper.insertRulesDB(rulesDTO);

                session.commit();
            }
        }
    }


    private void serializeExpression(SqlSession session, Expression expression, Integer previousParentId) {
        ExpressionsMapper expressionMapper = session.getMapper(ExpressionsMapper.class);

        for (Iterator<Expression> iterator =  expression.getExpressions().iterator(); iterator.hasNext(); ) {
            Expression subExpression = iterator.next();
            ExpressionsDTO expressionsDTO = (ExpressionsDTO) subExpression.accept(new DBWrapper(previousParentId));
            expressionMapper.insertElement(expressionsDTO);

            serializeExpression(session, subExpression, expressionsDTO.id);

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