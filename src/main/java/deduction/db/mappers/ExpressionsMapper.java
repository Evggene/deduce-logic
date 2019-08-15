package deduction.db.mappers;

import deduction.db.dto.ExpressionsDTO;
import java.util.List;

public interface ExpressionsMapper {
    void insert(ExpressionsDTO expressionDTO);
    void deleteExpression(int id);
    List<ExpressionsDTO> getChildExpressions(int expression_id);
}
