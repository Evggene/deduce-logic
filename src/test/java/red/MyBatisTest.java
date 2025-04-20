package red;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import red.deduction.db.dto.KnownFactsDTO;
import red.deduction.db.mappers.KnownFactsMapper;

public class MyBatisTest extends TestMain{

    @Autowired
    private KnownFactsMapper knownFactsMapper;

    @Test
    void a() {
        KnownFactsDTO dto = new KnownFactsDTO();
        dto.fact = "1";
        knownFactsMapper.insert(dto);
    }

}
