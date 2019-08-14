package deduction.db;

import deduction.Parser;
import deduction.ParserException;
import deduction.db.dto.ExpressionsDTO;
import deduction.db.dto.RulesDTO;
import deduction.db.mappers.ExpressionsMapper;
import deduction.db.mappers.KnownFactsMapper;
import deduction.db.mappers.RulesMapper;
import deduction.model.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;


public class DbParser implements Parser {

    private String configFile;


    public DbParser(String configFile) {
        this.configFile = configFile;
    }


    @Override
    public Model parse(String modelName) throws ParserException, FileNotFoundException {
        SqlSessionFactory ssf = new SqlSessionFactoryBuilder().build(new FileReader(configFile));
        try ( SqlSession session = ssf.openSession()) {

            KnownFactsMapper factsMapper = session.getMapper(KnownFactsMapper.class);
            Set<String> knownFacts = factsMapper.getKnownFacts(modelName);

            if (knownFacts.isEmpty()) {
                throw new ParserException(0, "Empty known facts");
            }
            RulesMapper rulesMapper = session.getMapper(RulesMapper.class);
            List<RulesDTO> rulesDTOList = rulesMapper.getRules(modelName);

            if (rulesDTOList.isEmpty()) {
                throw new ParserException(0, "Empty rules");
            }
            Collection<Rule> rulesList = new ArrayList<>();
            for (RulesDTO ruleDTO : rulesDTOList) {
                Expression expression = null;
                String type = ruleDTO.type_expression;

                if (type.equals("fact")) {
                    expression = new FactExpression(ruleDTO.fact);
                }
                if (type.equals("and")) {
                    expression = new AndExpression(getExpression(session, ruleDTO.id));
                }
                if (type.equals("or")) {
                    expression = new OrExpression(getExpression(session, ruleDTO.id));
                }
                System.out.println(expression);
                rulesList.add(new Rule(expression, ruleDTO.result_fact));
            }
            return new Model(rulesList, knownFacts);
        }
    }


    private Collection<Expression> getExpression(SqlSession session, int expressionId) {
        ExpressionsMapper expressionMapper = session.getMapper(ExpressionsMapper.class);
        ArrayList<Expression> currentExpression = new ArrayList<>();

        List<ExpressionsDTO> expressionsList = expressionMapper.getParentExpression(expressionId);
        for (ExpressionsDTO anExpressionsList : expressionsList) {
            String type = anExpressionsList.type_expression;
            if (type.equals("fact"))
                currentExpression.add(new FactExpression(anExpressionsList.fact));
            if (type.equals("and"))
                currentExpression.add(new AndExpression(getExpression(session, anExpressionsList.id)));
            if (type.equals("or"))
                currentExpression.add(new OrExpression(getExpression(session, anExpressionsList.id)));
        }
        return currentExpression;
    }
}
