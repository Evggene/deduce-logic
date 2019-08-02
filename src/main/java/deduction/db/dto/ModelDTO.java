package deduction.db.dto;

import lombok.Getter;
import lombok.Setter;



public class ModelDTO {

    public int id;
    public String name;

    public ModelDTO() {
    }

    public ModelDTO(String name) {
        this.name = name;
    }
}
