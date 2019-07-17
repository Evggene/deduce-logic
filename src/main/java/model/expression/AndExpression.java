package model.expression;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
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



    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Iterator<Expression> iterator = expressions.iterator(); iterator.hasNext(); ) {
            Expression expression = iterator.next();
            if (!(expression instanceof FactExpression)) {
                result.append("(" + expression + ")");
            }
            else {
                result.append(expression);
            }
            if (!iterator.hasNext()) {
                break;
            }
            result.append( " && ");
        }
        String sb = result.toString();
        return sb;
    }
}
