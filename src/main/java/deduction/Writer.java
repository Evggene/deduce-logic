package deduction;

import deduction.model.Model;

import java.io.IOException;


public interface Writer {
    void write(String filename, Model model) throws Exception, SerializerException;
    default void test(Model model) throws IOException {};
}
