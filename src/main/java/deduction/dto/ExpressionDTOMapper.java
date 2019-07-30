package deduction.dto;

import deduction.dto.domains.ExpressionDTO;

import java.util.List;

public interface ExpressionDTOMapper {

    void insertElement(ExpressionDTO expressionDTO);

    List<ExpressionDTO> getExpressionByRuleID(int ref_rules);

    void deleteExpression(int id);
}
