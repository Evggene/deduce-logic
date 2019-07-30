package deduction.dto;

import deduction.dto.domains.KnownFactsDTO;


import java.util.Set;

public interface KnownFactsDTOMapper {

    void insertKnownFacts(KnownFactsDTO knownfacts);

    Set<String> getKnownFactsByModelName(String name);

    void deleteKnownFacts(String name);
}

