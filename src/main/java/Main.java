import model.Model;
import parcer.Parser;
import parcer.ParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


public class Main {

    public static void main(String[] args) {

        if (args == null || args.length == 0) {
            System.out.print("Missing argument");
            return;
        }

        Model model;

        try {
            model = new Parser().parse(args[0]);
        } catch (FileNotFoundException e) {
            System.out.print("Wrong argument: file not found");
            return;
        } catch (IOException e) {
            System.out.print("Error when reading file: " + e.getMessage());
            return;
        } catch (ParserException e) {
            System.out.print("Invalid file: " + e.getMessage());
            return;
        } catch (Exception e) {
            System.out.print("Unknown error");
            return;
        }

        Collection<String> resultsList = null;

            resultsList = model.deduce();


        StringBuilder sb = new StringBuilder();
        Iterator<String> i = resultsList.iterator();
        while (i.hasNext()) {
            sb.append(i.next());
            if (! i.hasNext())
                System.out.print(sb);
            sb.append(',').append(' ');
        }
    }
}