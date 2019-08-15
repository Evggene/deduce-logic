package deduction.db.mappers;

import deduction.db.dto.RulesDTO;

import java.util.List;

public interface RulesMapper {
    void insert(RulesDTO rulesDTO);
    List<RulesDTO> getRules(String name);
    void deleteRule(int id);
}
