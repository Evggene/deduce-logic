

import deduction.Engine;
import deduction.ConsolePresenter;
import org.apache.commons.cli.*;


public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Empty arguments");
            return;
        }

        Option dbin = Option.builder("dbin").hasArg().numberOfArgs(2).desc("model name and config file name for writing from DB").build();
        Option txtin = Option.builder("txtin").hasArg().numberOfArgs(1).argName("file name").desc("input file in txt format").build();
        Option xmlin = Option.builder("xmlin").hasArg().numberOfArgs(1).argName("file name").desc("input file in xml format").build();

        Option txtout = Option.builder("txtout").hasArg().numberOfArgs(1).argName("file name").desc("output file in txt format").build();
        Option xmlout = Option.builder("xmlout").hasArg().numberOfArgs(1).argName("file name").desc("output file in txt format").build();
        Option dbout = Option.builder("dbout").hasArg().numberOfArgs(2).desc("model name and config file name for reading from DB").build();

        OptionGroup optionGroupIn = new OptionGroup();
        optionGroupIn
                .addOption(txtin)
                .addOption(xmlin)
                .addOption(dbin)
                .setRequired(true);
        OptionGroup optionGroupOut = new OptionGroup();
        optionGroupOut
                .addOption(txtout)
                .addOption(xmlout)
                .addOption(dbout);
        Options options = new Options();
        options
                .addOptionGroup(optionGroupIn)
                .addOptionGroup(optionGroupOut);

        HelpFormatter formatter = new HelpFormatter();

        CommandLine line;
        try {
            if (args[0].equals("-help")) {
                formatter.printHelp("help",
                        "Required one command of the following: " + System.lineSeparator() +
                                "'deduce' (accepts one of '-...in' option) : computes a set of passed rules " +
                                "'convert' (accepts one of '-...in' and '...out' options) : converts file from input to output format " + System.lineSeparator() +
                                "'delete' (accepts '-dbin' option) : removes a model from the database ",
                        options, "");
                return;
            }
            line = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return;
        }

        Engine engine = new Engine(new ConsolePresenter());
        String dbArgs[];
        String inputFile = null;
        String outputFile = null;
        Engine.FormatEnum formatInputFile = null;
        Engine.FormatEnum formatOutputFile = null;
        String inputConfig = null;
        String outputConfig = null;

        switch (args[0]) {
            case "deduce":
                if (line.getOptions().length != 1) {
                    System.out.println("Wrong number of arguments");
                    return;
                }
                if (line.getArgs().length != 1) {
                    System.out.println("Wrong number of arguments");
                    return;
                }
                if (line.hasOption("txtin")) {
                    inputFile = line.getOptionValue("txtin");
                    formatInputFile = Engine.FormatEnum.TXT;
                }
                if (line.hasOption("xmlin")) {
                    inputFile = line.getOptionValue("xmlin");
                    formatInputFile = Engine.FormatEnum.XML;
                }
                if (line.hasOption("dbin")) {
                    dbArgs = line.getOptionValues("dbin");
                    formatInputFile = Engine.FormatEnum.DB;
                    inputFile = dbArgs[0];
                    inputConfig = dbArgs[1];
                }
                engine.deduce(formatInputFile, inputFile, inputConfig);
                return;


            case "convert":
                if (line.getOptions().length != 2) {
                    System.out.println("Wrong number of options: redundant " + line.getOptions()[2].getOpt() + " with value " + line.getOptions()[2].getValue());
                    return;
                }
                if (line.getArgs().length != 1) {
                    System.out.println("Wrong number of arguments: " + line.getArgList().get(1));
                    return;
                }
                if (line.hasOption("txtin")) {
                    inputFile = line.getOptionValue("txtin");
                    formatInputFile = Engine.FormatEnum.TXT;
                }
                if (line.hasOption("xmlin")) {
                    inputFile = line.getOptionValue("xmlin");
                    formatInputFile = Engine.FormatEnum.XML;
                }
                if (line.hasOption("dbin")) {
                    formatInputFile = Engine.FormatEnum.DB;
                    dbArgs = line.getOptionValues("dbin");
                    inputFile = dbArgs[0];
                    inputConfig = dbArgs[1];
                }
                if (line.hasOption("txtout")) {
                    outputFile = line.getOptionValue("txtout");
                    formatOutputFile = Engine.FormatEnum.TXT;
                }
                if (line.hasOption("xmlout")) {
                    outputFile = line.getOptionValue("xmlout");
                    formatOutputFile = Engine.FormatEnum.XML;
                }
                if (line.hasOption("dbout")) {
                    formatOutputFile = Engine.FormatEnum.DB;
                    dbArgs = line.getOptionValues("dbout");
                    outputFile = dbArgs[0];
                    outputConfig = dbArgs[1];
                }
                engine.convert(inputFile, formatInputFile, inputConfig, outputFile, formatOutputFile, outputConfig);
                return;


            case "delete":
                if (line.getOptions().length != 1) {
                    System.out.println("Wrong number of arguments ");
                    return;
                }
                if (line.getArgs().length != 1) {
                    System.out.println("Wrong number of arguments: " + line.getArgList().get(1));
                    return;
                }
                if (line.hasOption("txtin")) {
                    inputFile = line.getOptionValue("txtin");
                    formatInputFile = Engine.FormatEnum.TXT;
                }
                if (line.hasOption("xmlin")) {
                    inputFile = line.getOptionValue("xmlin");
                    formatInputFile = Engine.FormatEnum.XML;
                }
                if (line.hasOption("dbin")) {
                    formatInputFile = Engine.FormatEnum.DB;
                    dbArgs = line.getOptionValues("dbin");
                    inputFile = dbArgs[0];
                    inputConfig = dbArgs[1];
                }
                engine.delete(formatInputFile, inputFile, inputConfig);
                return;


            default:
                System.out.println("Unknown command. Required: 'deduce', 'write', 'delete'");
        }
    }
}