package parcer;

import model.Model;
import model.Rule;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;


public class ParserXml {

    public Model parse(String filePath) throws SAXException, IOException, JAXBException, URISyntaxException {

        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema;
        schema = factory.newSchema(new StreamSource("C:\\Users\\Evgeniy.Bezlepkin\\Desktop\\EvgenyDeduce2\\src\\main\\resources\\scheme.xsd"));
        schema.newValidator().validate(new StreamSource(filePath));

        JAXBContext context = JAXBContext.newInstance(Model.class);
        Unmarshaller um = context.createUnmarshaller();
        Model model = (Model) um.unmarshal(new StreamSource(filePath));


// PRINT
        for (Rule rule : model.getRulesList()) {
            System.out.println(rule.getExpression());
        }

        return model;
    }
}



