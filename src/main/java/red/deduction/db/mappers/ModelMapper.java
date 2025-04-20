package red.deduction.db.mappers;

import org.apache.ibatis.annotations.Mapper;
import red.deduction.db.dto.ModelDTO;
import red.deduction.SerializerException;

@Mapper
public interface ModelMapper {
    void insert(ModelDTO modelDTO) ;
    void deleteModel(String name) ;
}
