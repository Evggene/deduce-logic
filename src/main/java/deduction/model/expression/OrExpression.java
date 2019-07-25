
package deduction.model.expression;

import javax.xml.bind.annotation.*;
import java.util.Collection;


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


    public Collection<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public String getStringPresentation() {
        return "Or";
    }

    @Override
    public String toString() {
        return "Or{" + expressions + '}';
    }
}
