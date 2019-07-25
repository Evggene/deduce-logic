package deduction.db.domains;

import lombok.Getter;
import lombok.Setter;

public class RulesDB {

    private @Getter @Setter int id;
    private @Getter @Setter String result_fact;
    private @Getter @Setter int ref_model;

    public RulesDB() {
    }

    public RulesDB(String result_fact, int ref_model) {
        this.result_fact = result_fact;
        this.ref_model = ref_model;
    }
}
