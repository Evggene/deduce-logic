
import java.io.File;

public class Main {

    public static void main(String[] args) {

        if (args == null || args.length < 2) {
            System.out.print("Missing argument");
            return;
        }

        if (args.length == 3 && !args[2].isEmpty()) {
            File file = new File(args[2]);
            if (file.exists()) {
                if (file.isFile()) {
                    System.out.println("File with current name is exist, choose new file name");
                    return;
                }
            }
            Deduction engine = new Deduction(args);
            engine.convert();
            System.out.println("Conversion is done");
        } else {
            Deduction engine = new Deduction(args);
            engine.deduce();
        }

    }

}