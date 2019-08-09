package deduction.db.dto;


public class ExpressionsDTO {

    public int id;
    private Integer parent_id;
    public String fact;
    private int type_id;
    public String type_expression;

    public ExpressionsDTO() {
    }

    public ExpressionsDTO(Integer parent_id, String fact, int type_id) {
        this.parent_id = parent_id;
        this.fact = fact;
        this.type_id = type_id;
    }

    public ExpressionsDTO(Integer parent_id, String fact, String type_expression) {
        this.parent_id = parent_id;
        this.fact = fact;
        this.type_expression = type_expression;
    }

}
