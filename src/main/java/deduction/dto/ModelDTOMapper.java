package deduction.dto;

import deduction.dto.domains.ModelDTO;

public interface ModelDTOMapper {

    void insertNameInModel(String name);

    int getModelIdByName(String name);

    void deleteModel(String name);
}

