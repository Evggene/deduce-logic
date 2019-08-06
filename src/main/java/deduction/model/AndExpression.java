package deduction.model;


import deduction.Wrapper;

import javax.xml.bind.annotation.*;
import java.util.Collection;


public class AndExpression implements Expression {

    @XmlElements({
            @XmlElement(name = "fact", type = FactExpression.class),
            @XmlElement(name = "or", type = OrExpression.class),
            @XmlElement(name = "and", type = AndExpression.class),
    })

    private Collection<Expression> expressions;

    public AndExpression() {
    }

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

    public Collection<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public Object accept(Wrapper t) {
        return t.wrap(this);
    }

    @Override
    public String toString() {
        return "And{" + expressions + '}';
    }
}
