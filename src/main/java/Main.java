

import deduction.Engine;
import org.apache.commons.cli.*;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;


public class Main {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Empty arguments");
            return;
        }

        Options options = new Options();

        Option dbin = Option.builder("dbin").hasArg().numberOfArgs(2).build();
        Option dbout = Option.builder("dbout").hasArg().numberOfArgs(2).build();

        options
                .addOption("txtin", true, "input file in txt format")
                .addOption("txtout", true, "output file in txt format")
                .addOption("xmlin", true, "input file in xml format")
                .addOption("xmlout", true, "output file in xml format")
                .addOption(dbin)
                .addOption(dbout);


        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return;
        }

        switch (args[0]) {

            case "deduce":
                if (line.getOptions().length != 1) {
                    System.err.println("Wrong number of arguments");
                    return;
                }
                String inputFile;
                Engine engine = new Engine();

                if (line.hasOption("txtin")) {
                    inputFile = line.getOptionValue("txtin");
                    engine.deduce(inputFile, Engine.FormatEnum.TXT);
                    return;
                }
                if (line.hasOption("xmlin")) {
                    inputFile = line.getOptionValue("xmlin");
                    engine.deduce(inputFile, Engine.FormatEnum.XML);
                    return;
                }
                if (line.hasOption("dbin")) {
                    String s[] = line.getOptionValues("dbin");
                    engine.createSqlSessionFactory(s[1]);
                    engine.deduce(s[0], Engine.FormatEnum.DB);
                    return;
                }
                System.err.println("Unknown key. Required: '-txtin', '-xmlin', '-dbin'");
                return;


            case "convert":
                if (line.getOptions().length != 2) {
                    System.err.println("Wrong number of arguments");
                    return;
                }
                inputFile = null;
                String outputFile = null;
                Engine.FormatEnum formatInputFile = null;
                engine = new Engine();

                if (line.hasOption("txtin")) {
                    inputFile = line.getOptionValue("txtin");
                    formatInputFile = Engine.FormatEnum.TXT;
                }
                if (line.hasOption("xmlin")) {
                    inputFile = line.getOptionValue("xmlin");
                    formatInputFile = Engine.FormatEnum.XML;
                }
                if (line.hasOption("dbin")) {
                    String s[] = line.getOptionValues("dbin");
                    engine.createSqlSessionFactory(s[1]);
                    inputFile = s[0];
                    formatInputFile = Engine.FormatEnum.DB;
                }
                if (line.hasOption("txtout")) {
                    outputFile = line.getOptionValue("txtout");
                    engine.convert(inputFile, formatInputFile, outputFile, Engine.FormatEnum.TXT);
                    return;
                }
                if (line.hasOption("xmlout")) {
                    outputFile = line.getOptionValue("xmlout");
                    engine.convert(inputFile, formatInputFile, outputFile, Engine.FormatEnum.XML);
                    return;
                }
                if (line.hasOption("dbout")) {
                    String s[] = line.getOptionValues("dbout");
                    engine.createSqlSessionFactory(s[1]);
                    engine.convert(inputFile, formatInputFile, s[0], Engine.FormatEnum.DB);
                    return;
                }
                System.err.println("Unknown key");
                return;


            case "delete":
                if (line.getOptions().length != 1) {
                    System.err.println("Wrong number of arguments");
                    return;
                }
                engine = new Engine();

                if (line.hasOption("dbin")) {
                    String s[] = line.getOptionValues("dbin");
                    engine.createSqlSessionFactory(s[1]);
                    engine.deleteDB(s[0]);
                    return;
                }
                System.err.println("Function 'delete' may be only from data base, therefore necessary key '-dto'");
                return;

            default:
                System.err.println("Unknown method. Required: 'deduce', 'convert', 'delete'");
        }

    }
}

