

import deduction.Engine;

import java.sql.SQLException;


public class Main {

    public static void main(String[] args) {


        if (args.length == 0) {
            System.out.println("Empty arguments");
            return;
        }

        switch (args[0]) {
            case "deduce":
                if (args.length != 3) {
                    System.out.print("Wrong number of arguments." +
                            "\n" + "Required: 'deduce -typeKey fileName'");
                    return;
                }
                Engine engine = new Engine();
                if (args[1].equals("-t")) {
                    engine.deduce(args[2], Engine.FormatEnum.TXT);
                    return;
                }
                if (args[1].equals("-x")) {
                    engine.deduce(args[2], Engine.FormatEnum.XML);
                    return;
                }
                if (args[1].equals("-db")) {
                    engine.deduce(args[2], Engine.FormatEnum.DB);
                    return;
                }
                System.out.println("Unknown key. Required: '-t', '-x', '-db'");
                return;

            case "convert":
                if (args.length != 5) {
                    System.out.print("Wrong number of arguments." +
                            "\n" + "Required: 'convert -typeInputKey inputFileName -typeOutputKey outputFileName'");
                    return;
                }
                Engine.FormatEnum formatInputFile = null;
                Engine.FormatEnum formatOutputFile;
                engine = new Engine();
                if (args[1].equals("-t")) {
                    formatInputFile = Engine.FormatEnum.TXT;
                }
                if (args[1].equals("-x")) {
                    formatInputFile = Engine.FormatEnum.XML;
                }
                if (args[1].equals("-db")) {
                    formatInputFile = Engine.FormatEnum.DB;
                }
                if (args[3].equals("-t")) {
                    formatOutputFile = Engine.FormatEnum.TXT;
                    engine.convert(args[2], formatInputFile, args[4], formatOutputFile);
                    return;
                }
                if (args[3].equals("-x")) {
                    formatOutputFile = Engine.FormatEnum.XML;
                    engine.convert(args[2], formatInputFile, args[4], formatOutputFile);
                    return;
                }
                if (args[3].equals("-db")) {
                    formatOutputFile = Engine.FormatEnum.DB;
                    engine.convert(args[2], formatInputFile, args[4], formatOutputFile);
                    return;
                }
                System.out.println("Unknown key. Required: '-t', '-x', '-db'");
                return;

            default:
                System.out.println("Unknown method. Required: 'deduce' or 'convert'");
        }


    }
}