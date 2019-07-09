import model.logic.ConsoleLogic;
import view.ConsoleView;

import java.util.*;


public class Main {

    public static void main(String[] args) {

        Collection<String> resultsList = new ConsoleLogic(args).makeResults();

        new ConsoleView(resultsList).viewResults();
    }
}