package model;

import model.expression.AndExpression;
import model.expression.Expression;
import model.expression.FactExpression;
import model.expression.OrExpression;

import javax.xml.bind.annotation.*;
import java.util.Collection;


@XmlAccessorType(XmlAccessType.FIELD)
public class Rule {


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

    void calculate(Collection<String> knownFacts) {
        if (expression.calculate(knownFacts)) {
                knownFacts.add(resultFact);
            }
        }

    @Override
    public String toString() {
        return "Rule{" +
                "expression=" + expression +
                ", resultFact='" + resultFact + '\'' +
                '}';
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public String getResultFact() {
        return resultFact;
    }

    public void setResultFact(String resultFact) {
        this.resultFact = resultFact;
    }
}