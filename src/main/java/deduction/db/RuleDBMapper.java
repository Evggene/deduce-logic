package deduction.db;

import deduction.db.domains.RuleDB;
import deduction.db.domains.RulesDB;

public interface RuleDBMapper {

    void insertRootFact(RuleDB ruleDB);

    void insertFact(RuleDB ruleDB);
    void insertNode(RuleDB ruleDB);
}
