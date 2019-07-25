package deduction.db.domains;

import lombok.Getter;
import lombok.Setter;

public class RuleDB {

    private @Getter @Setter int id;
    private @Getter @Setter int ref_rules;
    private @Getter @Setter int element_num;
    private @Getter @Setter Integer parent_id;
    private @Getter @Setter String fact;
    private @Getter @Setter String node;

    public RuleDB() {
    }

    public RuleDB(int ref_rules, int element_num, Integer parent_id, String fact, String node) {
        this.ref_rules = ref_rules;
        this.element_num = element_num;
        this.parent_id = parent_id;
        this.fact = fact;
        this.node = node;
    }
}
