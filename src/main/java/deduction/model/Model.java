

package deduction.model;

import javax.xml.bind.annotation.*;
import java.util.Collection;
import java.util.Set;


@XmlRootElement(name="deduce")
public class Model {

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


    public Collection<Rule> getRules() {
        return rulesList;
    }


    public Set<String> getKnownFacts() {
        return knownFactsList;
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
}