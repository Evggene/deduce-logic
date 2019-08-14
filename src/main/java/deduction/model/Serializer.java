package deduction.model;




import deduction.SerializerException;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public interface Serializer {
    void serializeModel(Collection<Rule> rulesList, Set<String> knownFactsList) throws SerializerException, IOException;
    void serializeRule(Expression expression, String resultFact) throws IOException, SerializerException;
    void serializeAndExpression(Collection<Expression> expressions) throws IOException, SerializerException;
    void serializeOrExpression(Collection<Expression> expressions) throws IOException, SerializerException;
    void serializeFactExpression(String fact) throws IOException, SerializerException;
}