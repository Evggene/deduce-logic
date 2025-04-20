package deduction;

import deduction.db.DbParser;
import deduction.model.Model;
import deduction.parser.Parser;
import deduction.parser.ParserException;
import deduction.presenter.ConsolePresenter;
import deduction.presenter.Presenter;
import deduction.txt.TxtParser;
import deduction.db.DbWriter;
import deduction.txt.TxtWriter;
import deduction.xml.XmlParser;
import deduction.xml.XmlWriter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.io.*;
import java.util.Collection;

public class Engine {

    public enum FormatEnum {
        TXT, XML, DB
    }
    private Presenter presenter;
    private String configFileName;

    public Engine(Presenter presenter) {
        this.presenter = presenter;
    }

    public void deduce(FormatEnum fmt, String fileName, String configFile) {
        if (fmt == FormatEnum.DB) {
            configFileName = configFile;
        }
        Model model;
        Parser parser;
        Collection<String> resultsList;
        try {
            parser = createParser(fmt);
            model = parser.parse(fileName);
            resultsList = model.deduce();
            presenter.showResult(resultsList);
        } catch (FileNotFoundException | ParserException e) {
            presenter.showError("Wrong argument: file not found");
            presenter.showError(e);
        } catch (IOException e) {
            presenter.showError("Error when reading file: ");
            presenter.showError(e);
        } catch (Exception e) {
            presenter.showError("Error: ");
            presenter.showError(e);
        }
    }

    public void convert(String inputFile, FormatEnum fmtin, String inputConfigFile, String outputFile, FormatEnum fmtout, String outputConfigFile) {
        Model model;
        Parser parser;
        if (fmtin == fmtout) {
            new ConsolePresenter().showError("Error: format input file and format output file are the same");
        }
        if (fmtin == FormatEnum.DB) {
            configFileName = inputConfigFile;
        }
        if (fmtout == FormatEnum.DB) {
            configFileName = outputConfigFile;
        }
        try {
            parser = createParser(fmtin);
            model = parser.parse(inputFile);
            Writer writer = createWriter(fmtout);
            writer.write(outputFile, model);
            presenter.showInfo("Conversion is done");
        } catch (IOException e) {
            presenter.showError(e);
        } catch (SerializerException e) {
            presenter.showError("Serializer Exception : ");
            presenter.showError(e);
        } catch (PersistenceException e) {
            presenter.showError("DB error : ");
            presenter.showError(e.getCause());
        } catch (Exception e) {
            presenter.showError("Unknown error : ");
            presenter.showError(e);
        }
    }

    public void delete(FormatEnum formatInputFile, String fileName, String dbConfig) throws Exception {
        if (formatInputFile == FormatEnum.DB) {
            configFileName = dbConfig;
        }
        Writer writer = createWriter(formatInputFile);
        try {
            writer.delete(fileName);
        } catch (Exception e) {
            presenter.showError(e);
        }
    }

    private Parser createParser(FormatEnum fmt) throws Exception {
        switch (fmt) {
            case TXT:
                return new TxtParser();
            case XML:
                return new XmlParser();
            case DB:
                return new DbParser(configFileName);
            default:
                throw new Exception("Unknown parser format");
        }
    }

    private Writer createWriter(FormatEnum fmt) throws Exception {
        switch (fmt) {
            case TXT:
                return new TxtWriter();
            case XML:
                return new XmlWriter();
            case DB:
                return new DbWriter(configFileName);
            default:
                throw new Exception("Unknown parser format");
        }
    }
}


