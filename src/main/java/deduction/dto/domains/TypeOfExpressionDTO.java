package deduction.dto.domains;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypeOfExpressionDTO {
    private int id;
    private String type_expression;

    public TypeOfExpressionDTO() {
    }

    public TypeOfExpressionDTO(int id) {
        this.id = id;
    }
}
