package deduction.dto.domains;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KnownFactsDTO {

    private int id;
    private int ref_model;
    private String fact;

    public KnownFactsDTO() {
    }

    public KnownFactsDTO(int ref_model, String fact) {
        this.ref_model = ref_model;
        this.fact = fact;
    }
}
