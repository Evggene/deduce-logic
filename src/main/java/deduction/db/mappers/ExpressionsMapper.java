package deduction.db.mappers;

import deduction.db.dto.DTO;
import deduction.db.dto.ExpressionsDTO;

import java.util.List;

public interface ExpressionsMapper extends Mapper {
    void insert(ExpressionsDTO expressionDTO);
    void deleteExpression(int id);
    List<ExpressionsDTO> getParentExpression(int expression_id);
}
