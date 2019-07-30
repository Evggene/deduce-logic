package deduction.dto;

import deduction.dto.domains.RulesDTO;

import java.util.List;


public interface RulesDTOMapper {

    int insertRulesDB(RulesDTO rulesDTO);

    List<RulesDTO> getRulesByModelName(String name);

    void deleteRules(int ref_model);

}
