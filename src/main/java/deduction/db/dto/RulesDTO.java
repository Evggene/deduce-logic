package deduction.db.dto;

import lombok.Getter;
import lombok.Setter;

public class RulesDTO {

    public int id;
    public String result_fact;
    public int model_id;

    public RulesDTO() {
    }

    public RulesDTO(String result_fact, int model_id) {
        this.result_fact = result_fact;
        this.model_id = model_id;
    }
}
