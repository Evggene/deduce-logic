package deduction;

import deduction.db.DbParser;


import deduction.model.Model;
import deduction.txt.TxtParser;
import deduction.db.DbSerializer;
import deduction.txt.TxtSerializer;
import deduction.xml.XmlParser;
import deduction.xml.XmlSerializer;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import java.io.*;
import java.util.Collection;


public class Engine {

    public enum FormatEnum {
        TXT, XML, DB
    }

    private SqlSessionFactory ssf;
    private Presenter presenter;

    public Engine(Presenter presenter) {
        this.presenter = presenter;
    }


    private void setDBConfig(String filename) {
        try {
            ssf = new SqlSessionFactoryBuilder().build(new FileReader(filename));
        } catch (FileNotFoundException e) {
            presenter.showError("File not found ");
            presenter.showError(e);
        }
    }

    public void deduce(FormatEnum fmt, String[] files) {
        if (fmt == FormatEnum.DB) {
            setDBConfig(files[1]);
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
        } catch (IOException e) {
            presenter.showError("Error when reading file: ");
        } catch (Exception e) {
            presenter.showError("Error: ");
        }
    }

    private Parser createParser(FormatEnum fmt) throws Exception {
        switch (fmt) {
            case TXT:
                return new TxtParser();
            case XML:
                return new XmlParser();
            case DB:
                return new DbParser(ssf);
            default:
                throw new Exception("Unknown parser format");
        }
    }

    public void convert(String inputFile, FormatEnum fmtin, String[] outputFile, FormatEnum fmtout) {
        Model model;
        Parser parser;

        if (fmtin == fmtout) {
            new ConsolePresenter().showError("Error: format input file and format output file are the same");
        }
        if (fmtout == FormatEnum.DB || fmtin == FormatEnum.DB) {
            setDBConfig(outputFile[1]);
        }
        try {
            parser = createParser(fmtin);
            model = parser.parse(inputFile);

            Writer writer = createWriter(fmtout);
            writer.write(outputFile[0], model);
            presenter.showInfo("Conversion is done");
        } catch (IOException e) {
            presenter.showError("Invalid argument: ");
        } catch (Exception e) {
            presenter.showError("Invalid file syntax: ");
            presenter.showError(e);
        }
    }

    private Writer createWriter(FormatEnum fmt) throws Exception {
        switch (fmt) {
            case TXT:
                return new TxtSerializer();
            case XML:
                return new XmlSerializer();
            case DB:
                return new DbSerializer(ssf);
            default:
                throw new Exception("Unknown parser format");
        }
    }

    public void deleteDB(String[] files) {
        setDBConfig(files[1]);
        try {
            DbSerializer writer = new DbSerializer(ssf);
            writer.deleteModelDB(files[0]);
        } catch (Exception e) {
            presenter.showError(e);
        }
    }
}


