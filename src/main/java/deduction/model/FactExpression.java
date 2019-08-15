package deduction.model;

import deduction.SerializerException;

import javax.xml.bind.annotation.*;
import java.io.IOException;
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
    public void serialize(Serializer serializer) throws IOException, SerializerException {
        serializer.serializeFactExpression(fact);
    }

    @Override
    public String toString() {
        return fact;
    }
}
