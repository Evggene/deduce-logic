package deduction.db.dto;


public class ExpressionsDTO implements DTO {

    public int id;
    public Integer parent_id;
    public String fact;
    public String type_expression;

    public ExpressionsDTO() {
    }

    public ExpressionsDTO(Integer parent_id, String fact, String type_expression) {
        this.parent_id = parent_id;
        this.fact = fact;
        this.type_expression = type_expression;
    }

}
