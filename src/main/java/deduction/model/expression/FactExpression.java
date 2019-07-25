package deduction.model.expression;

import javax.xml.bind.annotation.*;
import java.util.Collection;


public class FactExpression implements Expression {

    @XmlAttribute()
    private String fact;

    public FactExpression() {
    }

    public FactExpression(String fact) {
        this.fact = fact;
    }

    @Override
    public boolean calculate(Collection<String> knownFacts) {
        return knownFacts.contains(fact);
    }

    @Override
    public Collection<Expression> getExpressions() {
        return null;
    }

    @Override
    public String getStringPresentation() {
        return "Fact";
    }

    @Override
    public String toString() {
        return fact;
    }
}
