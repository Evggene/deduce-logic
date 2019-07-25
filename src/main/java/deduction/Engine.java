package deduction;

import deduction.parser.*;
import deduction.writer.Writer;
import deduction.model.Model;
import deduction.writer.WriterDB;
import deduction.writer.WriterTxt;
import deduction.writer.WriterXml;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;


public class Engine {

    public enum FormatEnum {
        TXT, XML, DB
    }


    public void deduce(String file, FormatEnum fmt) {
        Model model;
        Parser parser;
        Collection<String> resultsList;
        try {
            parser = createParser(fmt);
            model = parser.parse(file);
            resultsList = model.deduce();
        } catch (FileNotFoundException e) {
            System.out.print("Wrong argument: file not found");
            return;
        } catch (IOException e) {
            System.out.print("Error when reading file: " + e.getMessage());
            return;
        } catch (ParserException e) {
            System.out.print(e.getMessage());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("Error: " + e.getMessage());
            return;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> i = resultsList.iterator();
        if (i.hasNext())
            sb.append(i.next());
        while (i.hasNext()) {
            sb.append(", ").append(i.next());
        }
        System.out.print(sb);
    }


    private Parser createParser(FormatEnum fmt) throws Exception {
        switch (fmt) {
            case TXT:
                return new ParserTxt();
            case XML:
                return new ParserXml();
            case DB:
                return new ParserDB();
            default:
                throw new Exception("Unknown parser format");
        }
    }


    public void convert(String inputFile, FormatEnum fmtin, String outputFile, FormatEnum fmtout) {
        Model model;
        Parser parser;

        if (fmtin == fmtout) {
            System.out.println("Error: format input file and format output file are the same");
        } else
            try {
                parser = createParser(fmtin);
                model = parser.parse(inputFile);

                Writer writer = createWriter(fmtout);
                writer.convert(outputFile, model);
                System.out.print("Conversion is done");
            } catch (IOException e) {
                System.out.println("Invalid argument: " + e.getMessage());
            } catch (SAXException e) {
                System.out.println("Invalid file syntax: " + e.getMessage());
            } catch (JAXBException e) {
                System.out.println("Invalid file syntax: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Unknown error: " + e.getMessage());
            }
    }


    private Writer createWriter(FormatEnum fmt) throws Exception {
        switch (fmt) {
            case TXT:
                return new WriterTxt();
            case XML:
                return new WriterXml();
            case DB:
                return new WriterDB();
            default:
                throw new Exception("Unknown parser format");
        }
    }

}


