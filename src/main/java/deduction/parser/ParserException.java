package deduction.parser;

public class ParserException extends Exception{

    ParserException(int errorLine, String message) {
        super("Error in line " + errorLine + ": " + message);
    }
}
