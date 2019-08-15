package deduction.model;

import deduction.SerializerException;

import java.io.IOException;

public interface Serializable {
    void serialize(Serializer serializer) throws IOException, SerializerException;
}
