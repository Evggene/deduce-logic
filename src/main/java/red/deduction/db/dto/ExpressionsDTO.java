package red.deduction.db.dto;

public class ExpressionsDTO {
    public enum TypeExpression {
        and, or, fact
    }

    public int id;
    public Integer parent_id;
    public String fact;
    public TypeExpression type_expression;

    public ExpressionsDTO() {
    }

    public ExpressionsDTO(Integer parent_id, String fact, TypeExpression type_expression) {
        this.parent_id = parent_id;
        this.fact = fact;
        this.type_expression = type_expression;
    }
}
