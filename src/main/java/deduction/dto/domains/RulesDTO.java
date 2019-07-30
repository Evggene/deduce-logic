package deduction.dto.domains;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RulesDTO {

    private int id;
    private String result_fact;
    private int ref_model;

    public RulesDTO() {
    }

    public RulesDTO(String result_fact, int ref_model) {
        this.result_fact = result_fact;
        this.ref_model = ref_model;
    }
}
