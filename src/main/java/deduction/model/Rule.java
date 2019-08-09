package deduction.model;

import javax.xml.bind.annotation.*;

import java.util.Collection;


public class Rule implements Serializable {

    @XmlElements({
            @XmlElement(name = "fact", type = FactExpression.class),
            @XmlElement(name = "or", type = OrExpression.class),
            @XmlElement(name = "and", type = AndExpression.class),
    })
    private Expression expression;
    @XmlAttribute()
    private String resultFact;

    public Rule() {
    }

    public Rule(Expression expression, String resultFact) {
        this.expression = expression;
        this.resultFact = resultFact;
    }

    public void calculate(Collection<String> knownFacts) {
        if (expression.calculate(knownFacts)) {
            knownFacts.add(resultFact);
        }
    }

    @Override
    public void serialize(Serializer serializer) {
        serializer.serializeRule(expression, resultFact);
    }

}