import conversion.Conversion;
import conversion.ConversionInTxt;
import conversion.ConversionInXml;
import model.Model;
import org.xml.sax.SAXException;
import parcer.ParserTxt;
import parcer.ParserException;
import parcer.ParserXml;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;


public class Deduction {

    private String[] arguments;
    private Model model;


    public Deduction(String[] arguments) {
        this.arguments = arguments;
    }

    public void deduce() {
        Collection<String> resultsList;
        try {
            model = modelFabric();
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
            e.getMessage();
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


    public void convert() {
        try {
            model = modelFabric();
            Conversion conversion = conversionFabric();
            conversion.convert(arguments[2]);
        } catch (IOException e) {
            System.out.println("Invalid argument: " + e.getMessage());
            return;
        } catch (SAXException e) {
            System.out.println("Invalid file syntax: " + e.getMessage());
            return;
        } catch (JAXBException e) {
            System.out.println("Invalid file syntax: " + e.getMessage());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private Model modelFabric() throws Exception {
        if (arguments[1].equalsIgnoreCase("-t")) {
            ParserTxt engine = new ParserTxt();
            model = engine.parse(arguments[0]);
        } else if (arguments[1].equalsIgnoreCase("-x")) {
            ParserXml engine = new ParserXml();
            model = engine.parse(arguments[0]);
        } else {
            throw new ParserException(0, "Wrong key");
        }
        return model;
    }


    private Conversion conversionFabric() {
        Conversion conversion = null;
        if (arguments[1].equalsIgnoreCase("-t")) {
            conversion = new ConversionInXml(model);
        } else if (arguments[1].equalsIgnoreCase("-x")) {
            conversion = new ConversionInTxt(model);
        }
        return conversion;
    }


}
