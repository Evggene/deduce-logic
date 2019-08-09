package deduction.model;



import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;


public class FactExpression implements Expression, Serializable {
    @XmlAttribute()
    private String fact;

    public FactExpression() {
    }

    public FactExpression(String fact) {
        this.fact = fact;
    }

    @Override
    public boolean calculate(Collection<String> knownFacts) {
        return knownFacts.contains(fact);
    }

    @Override
    public void serialize(Serializer serializer) {
        serializer.serializeFactExpression(fact);
    }

    @Override
    public String toString() {
        return fact;
    }
}
