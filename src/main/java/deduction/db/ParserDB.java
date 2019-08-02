package deduction.db;


import deduction.Parser;
import deduction.ParserException;
import deduction.db.dto.ExpressionsDTO;
import deduction.db.dto.RulesDTO;
import deduction.db.mappers.ExpressionsMapper;
import deduction.db.mappers.KnownFactsMapper;
import deduction.db.mappers.RulesMapper;
import deduction.model.Model;
import deduction.model.Rule;
import deduction.model.expression.AndExpression;
import deduction.model.expression.Expression;
import deduction.model.expression.FactExpression;
import deduction.model.expression.OrExpression;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.*;


public class ParserDB implements Parser {

    private SqlSessionFactory ssf;


    public ParserDB(SqlSessionFactory ssf) {
        this.ssf = ssf;
    }

    @Override
    public Model parse(String modelName) throws ParserException {

        try (SqlSession session = ssf.openSession()) {

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

        for (int i = 0; i < expressionsList.size(); i++) {
            String type = expressionsList.get(i).type_expression;

            if (type.equals("fact"))
                currentExpression.add(new FactExpression(expressionsList.get(i).fact));
            if (type.equals("and"))
                currentExpression.add(new AndExpression(getExpression(session, expressionsList.get(i).id)));
            if (type.equals("or"))
                currentExpression.add(new OrExpression(getExpression(session, expressionsList.get(i).id)));
        }
        return currentExpression;
    }
}
