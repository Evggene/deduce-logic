package deduction.db.dto;


public class ModelDTO  implements DTO{

    public int id;
    public String name;

    public ModelDTO() {
    }

    public ModelDTO(String name) {
        this.name = name;
    }
}
