package deduction.parser;

import deduction.model.Model;

public interface Parser {
    Model parse(String filename) throws Exception;
}
