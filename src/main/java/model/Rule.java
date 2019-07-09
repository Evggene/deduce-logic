package model;

import model.expression.Expression;
import java.util.Collection;


public class Rule {

    private Expression expression;
    private String resultFact;


    public Rule(Expression expression, String resultFact) {
        this.expression = expression;
        this.resultFact = resultFact;
    }


    void calculate(Collection<String> knownFacts) {
        if (expression.calculate(knownFacts)) {
                knownFacts.add(resultFact);
            }
        }


    @Override
    public String toString() {
        return "Rule{" +
                "expression=" + expression +
                ", resultFact='" + resultFact + '\'' +
                '}';
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}