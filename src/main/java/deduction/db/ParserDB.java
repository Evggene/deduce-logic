package deduction.db;


import deduction.Parser;
import deduction.dto.*;
import deduction.dto.domains.ExpressionDTO;
import deduction.dto.domains.RulesDTO;
import deduction.dto.domains.TypeOfExpressionDTO;
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
    private int currentPos;

    public ParserDB(SqlSessionFactory ssf) {
        this.ssf = ssf;
    }

    @Override
    public Model parse(String modelName) {

        try (SqlSession session = ssf.openSession()) {

            KnownFactsDTOMapper factsMapper = session.getMapper(KnownFactsDTOMapper.class);
            Set<String> knownFacts = factsMapper.getKnownFactsByModelName(modelName);

            RulesDTOMapper rulesMapper = session.getMapper(RulesDTOMapper.class);
            List<RulesDTO> rulesDTOList = rulesMapper.getRulesByModelName(modelName);

            Collection<Rule> rulesList = new ArrayList<>();
            for (RulesDTO ruleDTO : rulesDTOList) {
                currentPos = 0;
                Expression expression = getExpressionFromRule(session, ruleDTO.getId());
                String resultFact = ruleDTO.getResult_fact();
//PRINT
                  System.out.println("e " + expression);
                rulesList.add(new Rule(expression, resultFact));
            }
            return new Model(rulesList, knownFacts);
        }
    }


    private Expression getExpressionFromRule(SqlSession session, int ruleId) {
        ExpressionDTOMapper ruleMapper = session.getMapper(ExpressionDTOMapper.class);
        List<HashMap<String, Object>> a = ruleMapper.getExpressionByRuleID(ruleId);

        for (; currentPos < a.size(); ) {
            String type = (String) a.get(currentPos).get("type_expression");
            String fact = (String) a.get(currentPos).get("fact");
            currentPos++;

            if (type.equals("fact")) {
                return new FactExpression(fact);
            }
            if (type.equals("and")) {
                return new AndExpression(getExpressionFromRule(a, 1));
            }
            if (type.equals("or")) {
                return new OrExpression(getExpressionFromRule(a, 1));
            }
        }
        return null;
    }


    private Collection<Expression> getExpressionFromRule(List<HashMap<String, Object>> ruleList, int previousNode_) {
        ArrayList<Expression> currentLine = new ArrayList<>();

        for (; currentPos < ruleList.size(); ) {
            String type = (String) ruleList.get(currentPos).get("type_expression");
            String fact = (String) ruleList.get(currentPos).get("fact");
            Integer parentId = (Integer) ruleList.get(currentPos).get("parent_id");
            int elementNum = (int) ruleList.get(currentPos).get("element_num");

            if (parentId != previousNode_)
                return currentLine;

            currentPos++;
            if (type.equals("fact"))
                currentLine.add(new FactExpression(fact));
            if (type.equals("and"))
                currentLine.add(new AndExpression(getExpressionFromRule(ruleList, elementNum)));
            if (type.equals("or"))
                currentLine.add(new OrExpression(getExpressionFromRule(ruleList, elementNum)));
        }
        return currentLine;
    }
}
