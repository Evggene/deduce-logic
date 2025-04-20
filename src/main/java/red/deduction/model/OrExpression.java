package red.deduction.model;

import red.deduction.SerializerException;

import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.util.Collection;

public class OrExpression implements Expression, Serializable {

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

    @Override
    public void serialize(Serializer serializer) throws IOException, SerializerException {
        serializer.serializeOrExpression(expressions);
    }

    @Override
    public String toString() {
        return "Or" + expressions ;
    }
}
