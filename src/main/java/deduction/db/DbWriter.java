package deduction.db;

import deduction.SerializerException;
import deduction.Writer;
import deduction.db.dto.*;
import deduction.db.mappers.*;
import deduction.model.Expression;
import deduction.model.Model;
import deduction.model.Rule;
import deduction.model.Serializer;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class DbWriter implements Writer, Serializer {

    private String configFile;
    private DbSessionWrapper session;
    private int modelId;
    private Integer expressionId;

    public DbWriter(String configFile) {
        this.configFile = configFile;
    }

    @Override
    public void write(String modelName, Model model) throws SerializerException, IOException {
        try {
            SqlSessionFactory ssf = new SqlSessionFactoryBuilder().build(new FileReader(configFile));
            session = new DbSessionWrapper(ssf);

            ModelMapper modelMapper = session.getMapper(ModelMapper.class);
            ModelDTO modelDTO = new ModelDTO(modelName);
            modelMapper.insert(modelDTO);
            modelId = modelDTO.id;
            model.serialize(this);
            session.commit();
        } finally {
            session.close();
        }
    }

    @Override
    public void serializeModel(Collection<Rule> rulesList, Set<String> knownFactsList) throws SerializerException, IOException {
        KnownFactsMapper knownFactsMapper = session.getMapper(KnownFactsMapper.class);
        for (String fact : knownFactsList) {
            knownFactsMapper.insert(new KnownFactsDTO(modelId, fact));
        }
        for (Rule rule : rulesList) {
            rule.serialize(this);
        }
    }

    @Override
    public void serializeRule(Expression expression, String resultFact) throws SerializerException, IOException {
        expressionId = null;
        expression.serialize(this);
        RulesMapper rulesDBMapper = session.getMapper(RulesMapper.class);
        RulesDTO rulesDTO = new RulesDTO(resultFact, modelId, expressionId);
        rulesDBMapper.insert(rulesDTO);
    }

    @Override
    public void serializeAndExpression(Collection<Expression> expressions) throws SerializerException, IOException {
        ExpressionsMapper expressionMapper = session.getMapper(ExpressionsMapper.class);
        ExpressionsDTO expressionsDTO = new ExpressionsDTO(expressionId, null, ExpressionsDTO.TypeExpression.and);
        expressionMapper.insert(expressionsDTO);
        for (Expression expression : expressions) {
            expressionId = expressionsDTO.id;
            expression.serialize(this);
        }
        expressionId = expressionsDTO.id;
    }

    @Override
    public void serializeOrExpression(Collection<Expression> expressions) throws SerializerException, IOException {
        ExpressionsMapper expressionMapper = session.getMapper(ExpressionsMapper.class);
        ExpressionsDTO expressionsDTO = new ExpressionsDTO(expressionId, null, ExpressionsDTO.TypeExpression.or);
        expressionMapper.insert(expressionsDTO);
        for (Expression expression : expressions) {
            expressionId = expressionsDTO.id;
            expression.serialize(this);
        }
        expressionId = expressionsDTO.id;
    }

    @Override
    public void serializeFactExpression(String fact) throws SerializerException {
        ExpressionsMapper expressionMapper = session.getMapper(ExpressionsMapper.class);
        ExpressionsDTO expressionsDTO = new ExpressionsDTO(expressionId, fact, ExpressionsDTO.TypeExpression.fact);
        expressionMapper.insert(expressionsDTO);
        expressionId = expressionsDTO.id;
    }

    public void delete(String modelName) throws SerializerException, FileNotFoundException {
        SqlSessionFactory ssf = new SqlSessionFactoryBuilder().build(new FileReader(configFile));
        try {
            session = new DbSessionWrapper(ssf);

            RulesMapper rulesMapper = session.getMapper(RulesMapper.class);
            List<RulesDTO> rulesDTOList = rulesMapper.getRules(modelName);

            ExpressionsMapper expressionDTOMapper = session.getMapper(ExpressionsMapper.class);
            for (RulesDTO ruleDB : rulesDTOList) {
                int expressionId = ruleDB.expression_id;
                rulesMapper.deleteRule(ruleDB.id);
                expressionDTOMapper.deleteExpression(expressionId);
                deleteExpression(expressionId);
            }
            KnownFactsMapper factsMapper = session.getMapper(KnownFactsMapper.class);
            factsMapper.deleteKnownFacts(modelName);

            ModelMapper modelMapper = session.getMapper(ModelMapper.class);
            modelMapper.deleteModel(modelName);
            session.commit();
            System.out.println("Delete model: " + "'" + modelName + "'" + " is successfully");
        } finally {
            session.close();
        }
    }

    private void deleteExpression(int id) throws SerializerException {
        ExpressionsMapper expressionDTOMapper = session.getMapper(ExpressionsMapper.class);
        expressionDTOMapper.deleteExpression(id);
        List<ExpressionsDTO> expressionsDTOList = expressionDTOMapper.getChildExpressions(id);
        for (ExpressionsDTO expressions : expressionsDTOList) {
            deleteExpression(expressions.id);
        }
    }

}