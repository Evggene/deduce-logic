package deduction.db.mappers;

import deduction.db.dto.ExpressionsDTO;

import java.util.List;

public interface ExpressionsMapper {


    void insertElement(ExpressionsDTO expressionDTO);

    void deleteExpression(int id);

    List<ExpressionsDTO> getParentExpression(int expression_id);
}
