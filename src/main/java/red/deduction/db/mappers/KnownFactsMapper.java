package red.deduction.db.mappers;

import org.apache.ibatis.annotations.Mapper;
import red.deduction.db.dto.KnownFactsDTO;


import java.util.Set;

@Mapper
public interface KnownFactsMapper {
    void insert(KnownFactsDTO knownFacts);
    Set<String> getKnownFacts(String name);
    void deleteKnownFacts(String name);
}

