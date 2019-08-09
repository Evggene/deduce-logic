package deduction.model;

import java.util.Collection;


public interface Expression {
    boolean calculate(Collection<String> knownFacts);
    void serialize (Serializer serializer);
}
