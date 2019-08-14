package deduction.db.mappers;

import deduction.db.dto.ModelDTO;
import deduction.SerializerException;

public interface ModelMapper extends Mapper{
    void insert(ModelDTO modelDTO) ;
    void deleteModel(String name) ;
}
