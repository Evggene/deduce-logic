package deduction.db.dto;

import lombok.Getter;
import lombok.Setter;


public class KnownFactsDTO {

    public int id;
    public int model_id;
    public String fact;

    public KnownFactsDTO() {
    }

    public KnownFactsDTO(int model_id, String fact) {
        this.model_id = model_id;
        this.fact = fact;
    }
}
