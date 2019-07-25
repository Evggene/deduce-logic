package deduction.db.domains;

import lombok.Getter;
import lombok.Setter;


public class Knownfacts {

    private @Getter @Setter int id;
    private @Getter @Setter int ref_model;
    private @Getter @Setter String fact;

    public Knownfacts() {
    }

    public Knownfacts(int ref_model, String fact) {
        this.ref_model = ref_model;
        this.fact = fact;
    }
}
