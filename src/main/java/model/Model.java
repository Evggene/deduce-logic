package model;

import java.util.Collection;
import java.util.List;


public class Model {
    private Collection<Rule> rulesList;
    private Collection<String> knowingFactsList;


    public Model(Collection<Rule> rulesList, Collection<String> knowingFactsList) {
        this.rulesList = rulesList;
        this.knowingFactsList = knowingFactsList;
    }


    public Collection<String> deduce() {
        int change = 0;
        for (int i = 0; i < rulesList.size(); i++) {
            for (int j = 0; j < rulesList.size()-i; j++) {
                Rule rule = (Rule)((List)rulesList).get(j);

                rule.calculate(knowingFactsList);
            }
            if (change == knowingFactsList.size()) {
                break;
            } else {
                change = knowingFactsList.size();
            }
        }
        return knowingFactsList;
    }
}