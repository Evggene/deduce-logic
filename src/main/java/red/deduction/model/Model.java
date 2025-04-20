package red.deduction.model;

import red.deduction.SerializerException;

import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@XmlRootElement(name = "deduce")
public class Model implements Serializable  {

    @XmlElementWrapper(name = "rules")
    @XmlElement(name = "rule")
    private Collection<Rule> rulesList;
    @XmlElementWrapper(name = "knownFacts")
    @XmlElement(name = "fact")
    private Set<String> knownFactsList;

    public Model() {
    }

    public Model(Collection<Rule> rulesList, Set<String> knownFactsList) {
        this.rulesList = rulesList;
        this.knownFactsList = knownFactsList;
    }

    public Collection<String> deduce() {
        int knownFactsSize = 0;
        while (knownFactsSize != knownFactsList.size()) {
            knownFactsSize = knownFactsList.size();
            for (Rule aRulesList : rulesList) {
                aRulesList.calculate(knownFactsList);
            }
        }
        return knownFactsList;
    }

    @Override
    public void serialize(Serializer serializer) throws IOException, SerializerException {
        serializer.serializeModel(rulesList, knownFactsList);
    }
    
}





