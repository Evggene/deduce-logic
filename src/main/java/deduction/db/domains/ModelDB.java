package deduction.db.domains;

import lombok.Getter;
import lombok.Setter;

public class ModelDB {

    @Getter @Setter private int id;
    @Getter @Setter private String name;


    public ModelDB() {
    }

    public ModelDB (String name) {
        this.name = name;
    }

}
