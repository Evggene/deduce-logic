package deduction.model.expression;

import java.util.Collection;


public interface Expression {

    boolean calculate(Collection<String> knownFacts);
    Collection<Expression> getExpressions();
    String getStringPresentation();
}
