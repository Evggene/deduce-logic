package deduction.dto;

import deduction.dto.domains.TypeOfExpressionDTO;

import java.util.List;

public interface TypeOfExpressionDTOMapper {

    List<TypeOfExpressionDTO> getIdByType();
}
