package model.expression;

import java.util.Collection;
import java.util.List;

public class AndExpression implements Expression{

    List<Expression> expressions;

    public AndExpression() {
    }

    @Override
    public boolean calculate(Collection<String> knownFacts) {
        for (Expression expression : expressions) {
            if (!expression.calculate(knownFacts)) {
                return false;
            }
        }
        return true;
    }


//    List<String> expressions;
//
//    public AndExpression(List<String> expressions) {
//        this.expressions = expressions;
//    }
//
//    @Override
//    public boolean calculate(Collection<String> knownFacts) {
//        for (String ex : expressions) {
//            if (knownFacts.contains(ex)) {
//                return false;
//            }
//        }
//        return true;
//    }
}
