package deduction.db;

import deduction.db.domains.ModelDB;
import deduction.model.Model;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ModelDBMapper {

    ModelDB getModelById(int id);
    void insertNameInModel(String name);
    ModelDB getModelByName(String name);
}

