package red.deduction.xml;

import red.deduction.model.Model;
import red.deduction.parser.Parser;

import org.xml.sax.SAXException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;

public class XmlParser implements Parser {

    public Model parse(String filename) throws SAXException, IOException, JAXBException {
        Schema schema;
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        File s = new File(getClass().getResource("/scheme.xsd").getFile());
        schema = factory.newSchema(new StreamSource(s));
        schema.newValidator().validate(new StreamSource(filename));

        JAXBContext context = JAXBContext.newInstance(Model.class);
        Unmarshaller um = context.createUnmarshaller();

        return (Model) um.unmarshal(new StreamSource(filename));
    }
}
