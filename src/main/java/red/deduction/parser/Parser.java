package red.deduction.parser;

import red.deduction.model.Model;

public interface Parser {
    Model parse(String filename) throws Exception;
}
