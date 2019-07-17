package model.expression;


import javax.xml.bind.annotation.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;


public class AndExpression implements Expression{

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

    @Override
    public String toString() {
        return "And" +
                expressions;
    }

    public Collection<Expression> getExpressions() {
        return expressions;
    }
}
