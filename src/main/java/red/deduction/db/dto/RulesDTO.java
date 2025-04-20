package red.deduction.db.dto;

public class RulesDTO {

    public int id;
    public String result_fact;
    public int model_id;
    public int expression_id;
    public String fact;
    public ExpressionsDTO.TypeExpression type_expression;

    public RulesDTO() {
    }

    public RulesDTO(String result_fact, int model_id, int expression_id) {
        this.result_fact = result_fact;
        this.model_id = model_id;
        this.expression_id = expression_id;
    }
}
