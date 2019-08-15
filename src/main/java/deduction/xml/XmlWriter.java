package deduction.xml;

import deduction.SerializerException;
import deduction.model.Model;
import deduction.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;

public class XmlWriter implements Writer {

     public void write(String filename, Model model) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Model.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(model, new File(filename));
    }

    @Override
    public void delete(String fileName) throws SerializerException, FileNotFoundException {
        File file = new File(fileName);
        if (!file.delete()) {
            throw new SerializerException("Cannot remove file");
        }
    }
}
