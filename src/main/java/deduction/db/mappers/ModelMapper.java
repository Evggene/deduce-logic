package deduction.db.mappers;

import deduction.db.dto.ModelDTO;

public interface ModelMapper {
    void insertModel(ModelDTO modelDTO);
    void deleteModel(String name);
}
