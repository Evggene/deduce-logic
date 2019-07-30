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

    private int currentPos = 0;
    private SqlSessionFactory ssf;
    private int indexOfFactType = 0, indexOfOrType = 0, indexOfAndType = 0;

    public ParserDB(SqlSessionFactory ssf) {
        this.ssf = ssf;
    }

    @Override
    public Model parse(String modelName) {

        try (SqlSession session = ssf.openSession()) {
            Collection<Rule> rulesList = new ArrayList<>();

            KnownFactsDTOMapper factsMapper = session.getMapper(KnownFactsDTOMapper.class);
            Set<String> knownFacts = factsMapper.getKnownFactsByModelName(modelName);

            RulesDTOMapper rulesMapper = session.getMapper(RulesDTOMapper.class);
            List<RulesDTO> rulesDTOList = rulesMapper.getRulesByModelName(modelName);

            TypeOfExpressionDTOMapper toe = session.getMapper(TypeOfExpressionDTOMapper.class);
            List<TypeOfExpressionDTO> typeOfExpression = toe.getIdByType();

            for (TypeOfExpressionDTO typeOfExpressionDTO : typeOfExpression) {
                if (typeOfExpressionDTO.getType().equals("fact"))
                    indexOfFactType = typeOfExpressionDTO.getId();
                if (typeOfExpressionDTO.getType().equals("or"))
                    indexOfOrType = typeOfExpressionDTO.getId();
                if (typeOfExpressionDTO.getType().equals("and"))
                    indexOfAndType = typeOfExpressionDTO.getId();
            }

            for (RulesDTO ruleDTO : rulesDTOList) {
                currentPos = 0;
                Expression expression = getExpressionFromRule(session, ruleDTO.getId());
                String resultFact = ruleDTO.getResult_fact();

    //PRINT
              //  System.out.println("e " + expression);
                rulesList.add(new Rule(expression, resultFact));
            }
            return new Model(rulesList, knownFacts);
        }
    }


    private Expression getExpressionFromRule(SqlSession session, int ruleId) {
        ExpressionDTOMapper ruleMapper = session.getMapper(ExpressionDTOMapper.class);
        List<ExpressionDTO> ruleList = ruleMapper.getExpressionByRuleID(ruleId);

        for (; currentPos < ruleList.size(); ) {
            Integer type = ruleList.get(currentPos).getType();
            String fact = ruleList.get(currentPos).getFact();
            currentPos++;

            if (type == indexOfFactType) {
                return new FactExpression(fact);
            }
            if (type == indexOfAndType) {
                return new AndExpression(getExpressionFromRule(ruleList, 1));
            }
            if (type == indexOfOrType) {
                return new OrExpression(getExpressionFromRule(ruleList, 1));
            }
        }
        return null;
    }


    private Collection<Expression> getExpressionFromRule(List<ExpressionDTO> ruleList, int previousNode_) {
        ArrayList<Expression> currentLine = new ArrayList<>();

        for (; currentPos < ruleList.size(); ) {
            Integer type = ruleList.get(currentPos).getType();
            String fact = ruleList.get(currentPos).getFact();
            Integer parentId = ruleList.get(currentPos).getParent_id();
            int elementNum = ruleList.get(currentPos).getElement_num();

            if (parentId != previousNode_)
                return currentLine;

            currentPos++;
            if (type == indexOfFactType)
                currentLine.add(new FactExpression(fact));
            if (type == indexOfAndType)
                currentLine.add(new AndExpression(getExpressionFromRule(ruleList, elementNum)));
            if (type == indexOfOrType)
                currentLine.add(new OrExpression(getExpressionFromRule(ruleList, elementNum)));
        }
        return currentLine;
    }
}
