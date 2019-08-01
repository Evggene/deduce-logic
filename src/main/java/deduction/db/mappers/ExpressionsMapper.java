package deduction.db.mappers;

import deduction.db.dto.ExpressionsDTO;

import java.util.List;

public interface ExpressionsMapper {


    void insertElement(ExpressionsDTO expressionDTO);

    List<ExpressionsDTO> getExpression(int ref_rules);

    void deleteExpression(int id);
}
