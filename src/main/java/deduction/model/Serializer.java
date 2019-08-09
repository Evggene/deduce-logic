package deduction.model;

import deduction.Writer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public interface Serializer {
    void serializeModel(Collection<Rule> rulesList, Set<String> knownFactsList);
    void serializeRule(Expression expression, String resultFact);
    void serializeAndExpression(Collection<Expression> expressions);
    void serializeOrExpression(Collection<Expression> expressions);
    void serializeFactExpression(String fact);
}