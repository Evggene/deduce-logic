package model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


public class Model {
    private Collection<Rule> rulesList;
    private Set<String> knownFactsList;


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
}