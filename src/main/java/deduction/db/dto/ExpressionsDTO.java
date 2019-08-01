package deduction.db.dto;

import lombok.Getter;
import lombok.Setter;



public class ExpressionsDTO {

    public int id;
    public int rules_id;
    public int element_num;
    public Integer parent_id;
    public String fact;
    public int type_id;
    public String type_expression;

    public ExpressionsDTO() {
    }

    public ExpressionsDTO(int rules_id, int element_num, Integer parent_id, String fact, int type_id) {
        this.rules_id = rules_id;
        this.element_num = element_num;
        this.parent_id = parent_id;
        this.fact = fact;
        this.type_id = type_id;
    }

    public ExpressionsDTO(int rules_id, int element_num, Integer parent_id, String fact, String type_expression) {
        this.rules_id = rules_id;
        this.element_num = element_num;
        this.parent_id = parent_id;
        this.fact = fact;
        this.type_expression = type_expression;
    }
}
