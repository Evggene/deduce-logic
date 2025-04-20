package red.deduction.db.mappers;

import org.apache.ibatis.annotations.Mapper;
import red.deduction.db.dto.RulesDTO;

import java.util.List;

@Mapper
public interface RulesMapper {
    void insert(RulesDTO rulesDTO);
    List<RulesDTO> getRules(String name);
    void deleteRule(int id);
}
