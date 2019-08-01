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
    private int currentPos;

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
                throw new ParserException(0, "Emty rules");
            }

            Collection<Rule> rulesList = new ArrayList<>();
            for (RulesDTO ruleDTO : rulesDTOList) {
                currentPos = 0;
                Expression expression = getExpressionFromRule(session, ruleDTO.id);
//PRINT
                //        System.out.println("e " + expression);
                rulesList.add(new Rule(expression, ruleDTO.result_fact));
            }
            return new Model(rulesList, knownFacts);
        }
    }


    private Expression getExpressionFromRule(SqlSession session, int ruleId) {
        ExpressionsMapper ruleMapper = session.getMapper(ExpressionsMapper.class);
        List<ExpressionsDTO> a = ruleMapper.getExpression(ruleId);

        for (; currentPos < a.size(); ) {
            String type = a.get(currentPos).type_expression;
            String fact = a.get(currentPos).fact;
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


    private Collection<Expression> getExpressionFromRule(List<ExpressionsDTO> ruleList, int previousNode_) {
        ArrayList<Expression> currentLine = new ArrayList<>();

        for (; currentPos < ruleList.size(); ) {
            String type = ruleList.get(currentPos).type_expression;
            String fact = ruleList.get(currentPos).fact;
            Integer parentId = ruleList.get(currentPos).parent_id;
            int elementNum = ruleList.get(currentPos).element_num;

            if (parentId != previousNode_){
                return currentLine;}

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
