package model.expression;

import java.util.Collection;


public class AndExpression implements Expression{

    private Collection<Expression> expressions;


    public AndExpression(Collection<Expression> expressions) {
        this.expressions = expressions;
    }

    @Override
    public boolean calculate(Collection<String> knownFacts) {
        for (Expression expression : expressions) {
            if (!expression.calculate(knownFacts)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "AndExpression{" +
                expressions +
                '}';
    }
}
