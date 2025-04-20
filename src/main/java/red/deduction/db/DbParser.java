package red.deduction.db;

import red.deduction.parser.Parser;
import red.deduction.parser.ParserException;
import red.deduction.db.dto.ExpressionsDTO;
import red.deduction.db.dto.RulesDTO;
import red.deduction.db.mappers.ExpressionsMapper;
import red.deduction.db.mappers.KnownFactsMapper;
import red.deduction.db.mappers.RulesMapper;
import red.deduction.model.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class DbParser implements Parser {

    private final String configFile;

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
                switch (ruleDTO.type_expression) {
                    case and:
                        expression = new AndExpression(parseExpressions(session, ruleDTO.id));
                        break;
                    case or:
                        expression = new OrExpression(parseExpressions(session, ruleDTO.id));
                        break;
                    case fact:
                        expression = new FactExpression(ruleDTO.fact);
                        break;
                }
      //PRINT
     //           System.out.println(expression);
                rulesList.add(new Rule(expression, ruleDTO.result_fact));
            }
            return new Model(rulesList, knownFacts);
        }
    }

    private Collection<Expression> parseExpressions(SqlSession session, int expressionId) {
        ExpressionsMapper expressionMapper = session.getMapper(ExpressionsMapper.class);
        ArrayList<Expression> currentExpressions = new ArrayList<>();

        List<ExpressionsDTO> expressionsList = expressionMapper.getChildExpressions(expressionId);
        for (ExpressionsDTO expressionDTO : expressionsList) {
            switch (expressionDTO.type_expression) {
                case and:
                    currentExpressions.add(new AndExpression(parseExpressions(session, expressionDTO.id)));
                    break;
                case or:
                    currentExpressions.add(new OrExpression(parseExpressions(session, expressionDTO.id)));
                    break;
                case fact:
                    currentExpressions.add(new FactExpression(expressionDTO.fact));
                    break;
            }
        }
        return currentExpressions;
    }
}
