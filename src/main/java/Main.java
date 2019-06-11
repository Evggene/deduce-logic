
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Main {

    public static void main(String[] args) {

        List<List<String>> rulesList = new ArrayList<>();
        List<String> factsList = new ArrayList<>();

        if (args == null || args.length == 0) {
            System.out.print("Missing argument");
            return;
        }

        Parser parser = new Parser(rulesList, factsList);
        try {
            parser.parse(args[0]);
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

        Model model = new Model(rulesList, factsList);
        model.deduce();


        StringBuilder resultList = new StringBuilder();
        for (int i = 0; i < factsList.size(); i++) {
            if (i < factsList.size() - 1) {
                resultList.append(factsList.get(i)).append(", ");
            } else {
                resultList.append(factsList.get(i));
            }
        }
        System.out.print(resultList);


    }
}