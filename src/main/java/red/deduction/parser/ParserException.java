package red.deduction.parser;

public class ParserException extends Exception{
    public ParserException(int errorLine, String message) {
        super("Error in line " + errorLine + ": " + message);
    }
}
