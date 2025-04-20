package red.deduction;

import red.deduction.model.Model;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Writer {
    void write(String filename, Model model) throws Exception;
    void delete(String fileName) throws SerializerException, FileNotFoundException;
}
