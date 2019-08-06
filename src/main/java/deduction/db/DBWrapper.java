package deduction.db;

import deduction.Wrapper;
import deduction.db.dto.DTO;
import deduction.db.dto.ExpressionsDTO;
import deduction.model.AndExpression;
import deduction.model.FactExpression;
import deduction.model.OrExpression;

public class DBWrapper implements Wrapper {

    private Integer parentId;


    DBWrapper(Integer parentId) {
        this.parentId = parentId;
    }

    @Override
    public DTO wrap(AndExpression andExpression) {
        return new ExpressionsDTO(parentId, null, "and");
    }

    @Override
    public DTO wrap(OrExpression orExpression) {
        return new ExpressionsDTO(parentId, null, "or");
    }

    @Override
    public DTO wrap(FactExpression factExpression) {
        return new ExpressionsDTO(parentId, factExpression.toString(), "fact");
    }

}
