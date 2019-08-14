package deduction.db;


import deduction.SerializerException;
import deduction.Writer;
import deduction.db.dto.ExpressionsDTO;
import deduction.db.dto.KnownFactsDTO;
import deduction.db.dto.ModelDTO;
import deduction.db.dto.RulesDTO;
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
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public class DbWriter implements Writer, Serializer {

    private String configFile;
    private DbWriterWrapper session;
    private int modelId;
    private Integer expressionId;
    private ArrayDeque<Integer> idNumbers;


    public DbWriter(String configFile) {
        this.configFile = configFile;
    }

    @Override
    public void write(String modelName, Model model) throws SerializerException, IOException {
        try {
            SqlSessionFactory ssf = new SqlSessionFactoryBuilder().build(new FileReader(configFile));
            session = new DbWriterWrapper(ssf);

            Mapper modelMapper = session.getMapper(ModelMapper.class);
            ModelDTO modelDTO = new ModelDTO(modelName);
            session.insert(modelMapper, modelDTO);
            modelId = modelDTO.id;
            model.serialize(this);
            session.commit();

        } finally {
            session.close();
        }
    }

    @Override
    public void serializeModel(Collection<Rule> rulesList, Set<String> knownFactsList) throws SerializerException, IOException {
        Mapper knownFactsMapper = session.getMapper(KnownFactsMapper.class);
        for (String fact : knownFactsList) {
            session.insert(knownFactsMapper, new KnownFactsDTO(modelId, fact));
        }
        for (Rule rule : rulesList) {
            rule.serialize(this);
        }
    }

    @Override
    public void serializeRule(Expression expression, String resultFact) throws SerializerException, IOException {
        idNumbers = new ArrayDeque<>();
        expressionId = null;
        expression.serialize(this);
        Mapper rulesDBMapper = session.getMapper(RulesMapper.class);
        RulesDTO rulesDTO = new RulesDTO(resultFact, modelId, expressionId);
        session.insert(rulesDBMapper, rulesDTO);
        idNumbers = null;
    }

    @Override
    public void serializeAndExpression(Collection<Expression> expressions) throws SerializerException, IOException {
        Mapper expressionMapper = session.getMapper(ExpressionsMapper.class);
        ExpressionsDTO expressionsDTO = new ExpressionsDTO(expressionId, null, "and");
        session.insert(expressionMapper, expressionsDTO);
        idNumbers.add(expressionsDTO.id);

        for (Expression expression : expressions) {
            expressionId = idNumbers.getLast();
            expression.serialize(this);
        }
        expressionId = idNumbers.pollLast();
    }

    @Override
    public void serializeOrExpression(Collection<Expression> expressions) throws SerializerException, IOException {
        Mapper expressionMapper = session.getMapper(ExpressionsMapper.class);
        ExpressionsDTO expressionsDTO = new ExpressionsDTO(expressionId, null, "or");
        session.insert(expressionMapper, expressionsDTO);
        idNumbers.add(expressionsDTO.id);

        for (Expression expression : expressions) {
            expressionId = idNumbers.getLast();
            expression.serialize(this);
        }
        expressionId = idNumbers.pollLast();
    }

    @Override
    public void serializeFactExpression(String fact) throws SerializerException {
        Mapper expressionMapper = session.getMapper(ExpressionsMapper.class);
        ExpressionsDTO expressionsDTO = new ExpressionsDTO(expressionId, fact, "fact");
        session.insert(expressionMapper, expressionsDTO);
        expressionId = expressionsDTO.id;
    }


    public void deleteModelDB(String modelName) throws SerializerException, FileNotFoundException {
        SqlSessionFactory ssf = new SqlSessionFactoryBuilder().build(new FileReader(configFile));
        try {
            session = new DbWriterWrapper(ssf);

            Mapper rulesMapper = session.getMapper(RulesMapper.class);

            List<RulesDTO> rulesDTOList = ((RulesMapper) rulesMapper).getRules(modelName);

            ExpressionsMapper expressionDTOMapper = session.getMapper(ExpressionsMapper.class);
            for (RulesDTO ruleDB : rulesDTOList) {

                expressionDTOMapper.deleteExpression(ruleDB.id);
                session.commit();
            }
            for (RulesDTO ruleDB : rulesDTOList) {
                ((RulesMapper) rulesMapper).deleteRules(ruleDB.id);
                session.commit();
            }
            KnownFactsMapper factsMapper = session.getMapper(KnownFactsMapper.class);
            factsMapper.deleteKnownFacts(modelName);
            session.commit();

            ModelMapper modelMapper = session.getMapper(ModelMapper.class);
            modelMapper.deleteModel(modelName);
            System.out.println("Delete model: " + "'" + modelName + "'" + " is successfully");
        } finally {
            session.close();
        }
    }

}