
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] pathToFile) {

        List<String> results;

        if (pathToFile == null || pathToFile.length == 0) {
            System.out.print("Missing argument");
            return;
        }

        Parser parser = new Parser(pathToFile[0]);

        try {
            int i = parser.validate();
            results = parser.parse(i, new Deduction());
        } catch (FileNotFoundException e) {
            System.out.print("Wrong argument: file not found");
            return;
        } catch (IOException e) {
            System.out.print("Error when reading file: " + e.getMessage());
            return;
        } catch (Exception e) {
            System.out.print("File does not valid: " + e.getMessage());
            return;
        }


        StringBuilder finResult = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            if (i < results.size() - 1) {
                finResult.append(results.get(i) + ", ");
            } else {
                finResult.append(results.get(i));
            }
        }
        System.out.print(finResult);
    }
}


