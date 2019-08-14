package deduction.model;

import deduction.SerializerException;

import java.io.IOException;
import java.util.Collection;


public interface Expression {
    boolean calculate(Collection<String> knownFacts);
    void serialize (Serializer serializer) throws IOException, SerializerException;
}
