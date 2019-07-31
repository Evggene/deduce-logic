package deduction.dto.domains;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ExpressionDTO {

    private int id;
    private int ref_rules;
    private int element_num;
    private Integer parent_id;
    private String fact;
    private int type;

    public ExpressionDTO() {
    }

    public ExpressionDTO(int ref_rules, int element_num, Integer parent_id, String fact, int type) {
        this.ref_rules = ref_rules;
        this.element_num = element_num;
        this.parent_id = parent_id;
        this.fact = fact;
        this.type = type;
    }

}
