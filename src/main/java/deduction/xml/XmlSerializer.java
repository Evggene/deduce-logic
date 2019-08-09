package deduction.xml;




import deduction.model.Model;
import deduction.Writer;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;


public class XmlSerializer implements Writer {

     public void write(String filename, Model model) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(Model.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(model, new File(filename));
    }




}
