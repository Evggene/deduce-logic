
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Main {

    public static void main(String[] args) {

        if (args == null || args.length == 0) {
            System.out.print("Missing argument");
            return;
        }


        Parser parser = new Parser();
        Model model = null;


        try {
            model = parser.parse(args[0]);
        } catch (FileNotFoundException e) {
            System.out.print("Wrong argument: file not found");
            return;
        } catch (IOException e) {
            System.out.print("Error when reading file: " + e.getMessage());
            return;
        } catch (ParserException e) {
            System.out.print("File does not valid: " + e.getMessage());
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }


        List resultsList = model.deduce();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < resultsList.size(); i++) {
            if (i < resultsList.size() - 1) {
                sb.append(resultsList.get(i)).append(", ");
            } else {
                sb.append(resultsList.get(i));
            }
        }
        System.out.print(sb);

        Main m = new Main();


    }
}