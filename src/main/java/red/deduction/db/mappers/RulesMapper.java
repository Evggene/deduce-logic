package red.deduction.db.mappers;

import red.deduction.db.dto.RulesDTO;

import java.util.List;

public interface RulesMapper {
    void insert(RulesDTO rulesDTO);
    List<RulesDTO> getRules(String name);
    void deleteRule(int id);
}
