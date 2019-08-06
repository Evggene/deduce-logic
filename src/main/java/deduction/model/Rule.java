package deduction.model;

import lombok.Getter;

import javax.xml.bind.annotation.*;
import java.util.Collection;



public class Rule {

    @XmlElements({
            @XmlElement(name = "fact", type = FactExpression.class),
            @XmlElement(name = "or", type = OrExpression.class),
            @XmlElement(name = "and", type = AndExpression.class),
    })
    private @Getter Expression expression;
    @XmlAttribute()
    private @Getter String resultFact;

    public Rule() {
    }

    public Rule(Expression expression, String resultFact) {
        this.expression = expression;
        this.resultFact = resultFact;
    }

    void calculate(Collection<String> knownFacts) {
        if (expression.calculate(knownFacts)) {
                knownFacts.add(resultFact);
            }
        }

}