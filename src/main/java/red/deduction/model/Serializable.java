package red.deduction.model;

import red.deduction.SerializerException;

import java.io.IOException;

public interface Serializable {
    void serialize(Serializer serializer) throws IOException, SerializerException;
}
