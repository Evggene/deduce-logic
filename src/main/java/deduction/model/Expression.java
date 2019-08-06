package deduction.model;

import deduction.Wrapper;

import java.util.Collection;


public interface Expression {

    boolean calculate(Collection<String> knownFacts);
    Collection<Expression> getExpressions();
    Object accept ( Wrapper t );

}
