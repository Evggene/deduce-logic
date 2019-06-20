package model.expression;

import java.util.Collection;


public class OrExpression implements Expression {

    private Collection<Expression> expressions;


    public OrExpression(Collection<Expression> expressions) {
        this.expressions = expressions;
    }

    @Override
    public boolean calculate(Collection<String> knownFacts) {
        for (Expression expression : expressions) {
            if (expression.calculate(knownFacts)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "OrExpression{" +
                expressions +
                '}';
    }
}
