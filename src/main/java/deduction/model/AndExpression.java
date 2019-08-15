package deduction.model;

import deduction.SerializerException;

import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.util.Collection;

public class AndExpression implements Expression, Serializable {

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
    public void serialize(Serializer serializer) throws IOException, SerializerException {
        serializer.serializeAndExpression(expressions);
    }

    @Override
    public String toString() {
        return "And" + expressions ;
    }
}
