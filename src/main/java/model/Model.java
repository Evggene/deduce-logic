

package model;

import com.sun.xml.internal.txw2.annotation.XmlNamespace;

import javax.xml.bind.annotation.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


@XmlRootElement(name="deduce")
@XmlAccessorType(XmlAccessType.FIELD)
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


    public Collection<String> deduce() {
        int knownFactsSize = 0;
        while (knownFactsSize != knownFactsList.size()) {
            knownFactsSize = knownFactsList.size();
            for (Iterator<Rule> iter = rulesList.iterator(); iter.hasNext(); ) {
                iter.next().calculate(knownFactsList);
            }
        }
        return knownFactsList;
    }

    public Collection<Rule> getRulesList() {
        return rulesList;
    }

    public void setRulesList(Collection<Rule> rulesList) {
        this.rulesList = rulesList;
    }

    public Set<String> getKnownFactsList() {
        return knownFactsList;
    }

    public void setKnownFactsList(Set<String> knownFactsList) {
        this.knownFactsList = knownFactsList;
    }
}