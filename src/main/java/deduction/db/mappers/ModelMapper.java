package deduction.db.mappers;

public interface ModelMapper {

    void insertNameInModel(String name);

    int getModelId(String name);

    void deleteModel(String name);

}

