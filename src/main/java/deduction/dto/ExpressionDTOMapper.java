package deduction.dto;

import deduction.dto.domains.ExpressionDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ExpressionDTOMapper {


    void insertElement(ExpressionDTO expressionDTO);

    List<HashMap<String, Object>> getExpressionByRuleID(int ref_rules);
    //List<ExpressionDTO> getExpressionByRuleID(int ref_rules);

    void deleteExpression(int id);
}
