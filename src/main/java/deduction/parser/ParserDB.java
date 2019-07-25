package deduction.parser;


import deduction.ConnectionPostgres;
import deduction.model.Model;
import deduction.model.Rule;
import deduction.model.expression.AndExpression;
import deduction.model.expression.Expression;
import deduction.model.expression.FactExpression;
import deduction.model.expression.OrExpression;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class ParserDB implements Parser {


    @Override
    public Model parse(String modelName) throws SQLException {

        try (Connection connection = ConnectionPostgres.connect();
             Statement statement = connection.createStatement()) {

            Set<String> knownFacts = null;
            Collection<Rule> rulesList = new ArrayList<>();

            ResultSet modelIdSet = statement.executeQuery("SELECT id FROM model where name = '" + modelName + "'");
            int modelId = 0;
            while (modelIdSet.next()) {
                modelId = modelIdSet.getInt(1);
            }

            knownFacts = getKnownFacts(modelId);

            ResultSet rulesSet = statement.executeQuery("SELECT id, result_fact, ref_model " +
                    "FROM rules where ref_model = '" + modelId + "'");

            Expression expression = null;
            String resultFact = null;

            while (rulesSet.next()) {
                expression = getExpressionFromRule(rulesSet.getInt("id"));
                resultFact = rulesSet.getString("result_fact");

                System.out.println(expression);
                rulesList.add(new Rule(expression, resultFact));
            }

            return new Model(rulesList, knownFacts);
        }
    }

    private Expression getExpressionFromRule(int ruleId) throws SQLException {
        try (Connection connection = ConnectionPostgres.connect();
             Statement statement = connection.createStatement()) {
            ResultSet expressionSet = statement.executeQuery("SELECT element_num, parent_id, fact, node " +
                    "FROM rule where ref_rules = '" + ruleId + "'");

            Expression expression = null;

            while (expressionSet.next()) {
                String fact = expressionSet.getString("fact");
                int parentId = expressionSet.getInt("parent_id");
                String node = expressionSet.getString("node");

                if (parentId == 0)
                    if (fact != null)
                        return new FactExpression(fact);
                if (node.equals("And"))
                    return new AndExpression(recursive(expressionSet, 1));
                if (node.equals("Or"))
                    return new OrExpression(recursive(expressionSet, 1));
            }


            return expression;
        }
    }

    private Collection<Expression> recursive(ResultSet expressionSet, int previousNode_) throws SQLException {
        ArrayList<Expression> currentLine = new ArrayList<>();
        while (expressionSet.next()) {

            String fact = expressionSet.getString("fact");
            String node = expressionSet.getString("node");
            int parentId = expressionSet.getInt("parent_id");
            int elementNum = expressionSet.getInt("element_num");
            int previousNode = previousNode_;

            if (fact != null) {
                currentLine.add(new FactExpression(fact));
            } else {
                if (node.equals("And"))
                    if (previousNode_ == parentId) {
                        currentLine.add(new AndExpression(recursive(expressionSet, elementNum)));
                    } else {
                        return currentLine;
                    }
                if (node.equals("Or"))
                    if (previousNode_ == parentId) {
                        currentLine.add(new OrExpression(recursive(expressionSet, elementNum)));
                    } else {
                        return currentLine;
                    }
            }

        }
        return currentLine;
    }


    private Set<String> getKnownFacts(int modelId) throws SQLException {
        try (Connection connection = ConnectionPostgres.connect();
             Statement statement = connection.createStatement()) {
            HashSet<java.lang.String> knownFacts = new HashSet<>();
            ResultSet knownFactsSet = statement.executeQuery("SELECT fact FROM knownfacts where ref_model = '" + modelId + "'");

            while (knownFactsSet.next())
                knownFacts.add(knownFactsSet.getString(1));
            return knownFacts;
        }
    }
}








