package deduction.writer;

public class WriterException extends Exception {

    WriterException(int errorLine, String message) {
        super("Error in line " + errorLine + ": " + message);
    }
}
