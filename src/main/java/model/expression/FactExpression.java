package model.expression;

import java.util.Collection;

public class FactExpression implements Expression {

    String fact;

    public FactExpression(String fact) {
        this.fact = fact;
    }

    @Override
    public boolean calculate(Collection<String> knownFacts) {
     return knownFacts.contains(fact);
    }
}
