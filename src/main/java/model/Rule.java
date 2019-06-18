package model;


import model.expression.Expression;

import java.util.ArrayList;
import java.util.Collection;

public class Rule {

    Expression expression;
    String resultFact;


    public Rule(Expression expression, String resultFact) {
        this.expression = expression;
        this.resultFact = resultFact;
    }


    void calculate(Collection<String> knownFacts) {
        if (expression.calculate(knownFacts)) {
            if (!knownFacts.contains(resultFact)) {
                knownFacts.add(resultFact);
            }
        }
    }


}
