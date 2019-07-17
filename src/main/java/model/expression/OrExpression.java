
package model.expression;

import javax.xml.bind.annotation.*;
import java.util.Collection;
import java.util.Iterator;


public class OrExpression implements Expression {


    @XmlElements({
            @XmlElement(name = "fact", type = FactExpression.class),
            @XmlElement(name = "or", type = OrExpression.class),
            @XmlElement(name = "and", type = AndExpression.class),
    })
    private Collection<Expression> expressions;

    public OrExpression() {
    }

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



    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Iterator<Expression> iterator = expressions.iterator(); iterator.hasNext(); ) {
            Expression expression = iterator.next();
                result.append(expression);
            if (!iterator.hasNext()) {
                break;
            }
            result.append( " || ");
        }
        String sb = result.toString();
        return sb;
    }
}
