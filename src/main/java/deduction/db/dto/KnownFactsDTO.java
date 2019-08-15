package deduction.db.dto;

public class KnownFactsDTO  {

    public int model_id;
    public String fact;

    public KnownFactsDTO() {
    }

    public KnownFactsDTO(int model_id, String fact) {
        this.model_id = model_id;
        this.fact = fact;
    }
}
