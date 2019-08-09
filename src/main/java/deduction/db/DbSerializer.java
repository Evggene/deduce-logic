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

import java.util.Collection;

import java.util.List;
import java.util.Set;


public class DbSerializer implements Writer, Serializer {


    public DbSerializer(SqlSessionFactory ssf) {
        this.ssf = ssf;
    }

    private SqlSessionFactory ssf;
    private Integer modelId;
    private int depth = -1;
    private int ruleId;
    private Integer parentId;


    @Override
    public void write(String modelName, Model model) {

        try (SqlSession session = ssf.openSession(false)) {

            ModelMapper modelMapper = session.getMapper(ModelMapper.class);
            ModelDTO modelDTO = new ModelDTO(modelName);
            modelMapper.insertModel(modelDTO);
            modelId = modelDTO.id;
            session.commit();
            model.serialize(this);
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


    @Override
    public void serializeModel(Collection<Rule> rulesList, Set<String> knownFactsList) {
        try (SqlSession session = ssf.openSession(false)) {
            KnownFactsMapper knownFactsMapper = session.getMapper(KnownFactsMapper.class);
            for (String fact : knownFactsList) {
                knownFactsMapper.insertKnownFacts(new KnownFactsDTO(modelId, fact));
            }
            for (Rule rule : rulesList) {
                rule.serialize(this);
            }
            session.commit();
        }
    }

    @Override
    public void serializeRule(Expression expression, String resultFact) {
        try (SqlSession session = ssf.openSession(false)) {
            expression.serialize(this);
            RulesMapper rulesDBMapper = session.getMapper(RulesMapper.class);
            RulesDTO rulesDTO = new RulesDTO(resultFact, modelId, ruleId - depth);
            rulesDBMapper.insertRulesDB(rulesDTO);
            session.commit();
            depth = -1;
            parentId = null;
        }
    }

    @Override
    public void serializeAndExpression(Collection<Expression> expressions) {
        try (SqlSession session = ssf.openSession(false)) {
            ExpressionsMapper expressionMapper = session.getMapper(ExpressionsMapper.class);
            ExpressionsDTO expressionsDTO = new ExpressionsDTO(parentId, null, "and");
            expressionMapper.insertElement(expressionsDTO);
            session.commit();
            ruleId = expressionsDTO.id;
            depth++;
            parentId = ruleId;
            for (Expression expression : expressions) {
                expression.serialize(this);
            }
            parentId--;
        }
    }

    @Override
    public void serializeOrExpression(Collection<Expression> expressions) {
        try (SqlSession session = ssf.openSession(false)) {
            ExpressionsMapper expressionMapper = session.getMapper(ExpressionsMapper.class);
            ExpressionsDTO expressionsDTO = new ExpressionsDTO(parentId, null, "or");
            expressionMapper.insertElement(expressionsDTO);
            session.commit();
            ruleId = expressionsDTO.id;
            depth++;
            parentId = ruleId;
            for (Expression expression : expressions) {
                expression.serialize(this);
            }
            parentId--;
        }
    }

    @Override
    public void serializeFactExpression(String fact) {
        try (SqlSession session = ssf.openSession(false)) {
            ExpressionsMapper expressionMapper = session.getMapper(ExpressionsMapper.class);
            ExpressionsDTO expressionsDTO = new ExpressionsDTO(parentId, fact, "fact");
            expressionMapper.insertElement(expressionsDTO);
            session.commit();
            ruleId = expressionsDTO.id;
            depth++;
        }
    }
}