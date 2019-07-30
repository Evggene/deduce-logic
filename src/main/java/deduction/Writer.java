package deduction;


import deduction.model.Model;

public interface Writer {
    void convert(String filename, Model model) throws Exception;
}
