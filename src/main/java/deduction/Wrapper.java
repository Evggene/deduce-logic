package deduction;

import deduction.model.AndExpression;
import deduction.model.FactExpression;
import deduction.model.OrExpression;


public interface Wrapper {
    Object wrap(AndExpression andExpression);
    Object wrap(OrExpression orExpression);
    Object wrap(FactExpression factExpression);

}
