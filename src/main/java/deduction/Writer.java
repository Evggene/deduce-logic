package deduction;


import deduction.model.Model;

public interface Writer {
    void write(String filename, Model model) throws Exception;
}
