package red.deduction.db.mappers;

import red.deduction.db.dto.ModelDTO;
import red.deduction.SerializerException;

public interface ModelMapper {
    void insert(ModelDTO modelDTO) ;
    void deleteModel(String name) ;
}
