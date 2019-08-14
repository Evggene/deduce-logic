package deduction;

import deduction.db.DbParser;


import deduction.model.Model;
import deduction.txt.TxtParser;
import deduction.db.DbWriter;
import deduction.txt.TxtWriter;
import deduction.xml.XmlParser;
import deduction.xml.XmlSerializer;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import java.io.*;
import java.util.Collection;


public class Engine {

    public enum FormatEnum {
        TXT, XML, DB
    }

    public static class ConvertWrap {
        String inputFile;
        FormatEnum fmtin;
        String inputConfigFile;
        String outputFile;
        FormatEnum fmtout;
        String outputConfigFile;

        public ConvertWrap(String inputFile, FormatEnum fmtin, String inputConfigFile, String outputFile, FormatEnum fmtout, String outputConfigFile) {
            this.inputFile = inputFile;
            this.fmtin = fmtin;
            this.inputConfigFile = inputConfigFile;
            this.outputFile = outputFile;
            this.fmtout = fmtout;
            this.outputConfigFile = outputConfigFile;
        }
    }


    private Presenter presenter;
    private String configFileName;

    public Engine(Presenter presenter) {
        this.presenter = presenter;
    }


    public void deduce(FormatEnum fmt, String[] files) {
        if (fmt == FormatEnum.DB) {
            configFileName = files[1];
        }
        Model model;
        Parser parser;
        Collection<String> resultsList;
        try {
            parser = createParser(fmt);
            model = parser.parse(files[0]);
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

    public void convert(ConvertWrap convertWrap) {
        String inputFile = convertWrap.inputFile;
        FormatEnum fmtin = convertWrap.fmtin;
        String inputConfigFile = convertWrap.inputConfigFile;
        String outputFile = convertWrap.outputFile;
        FormatEnum fmtout = convertWrap.fmtout;
        String outputConfigFile = convertWrap.outputConfigFile;

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
            e.printStackTrace();
            presenter.showError("DB error : ");
            presenter.showError(e.getCause());

        } catch (Exception e) {
            presenter.showError("Unknown error : ");
            presenter.showError(e);
            e.printStackTrace();
        }
    }

    private Writer createWriter(FormatEnum fmt) throws Exception {
        switch (fmt) {
            case TXT:
                return new TxtWriter();
            case XML:
                return new XmlSerializer();
            case DB:
                return new DbWriter(configFileName);
            default:
                throw new Exception("Unknown parser format");
        }

    }

    public void deleteDB(String[] files) {
        configFileName = files[1];
        try {
            DbWriter writer = new DbWriter(configFileName);
            writer.deleteModelDB(files[0]);
        } catch (Exception e) {
            presenter.showError(e);
        }
    }
}


