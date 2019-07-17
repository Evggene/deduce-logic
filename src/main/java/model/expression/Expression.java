package model.expression;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Collection;


public interface Expression {

    boolean calculate(Collection<String> knownFacts);

}
