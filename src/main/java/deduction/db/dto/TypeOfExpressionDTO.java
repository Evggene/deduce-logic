package deduction.db.dto;

import lombok.Getter;
import lombok.Setter;


public class TypeOfExpressionDTO {
    public int id;
    public String type_expression;

    public TypeOfExpressionDTO() {
    }

    public TypeOfExpressionDTO(int id) {
        this.id = id;
    }
}
