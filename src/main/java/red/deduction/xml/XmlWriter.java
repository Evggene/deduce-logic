package red.deduction.xml;

import red.deduction.SerializerException;
import red.deduction.model.Model;
import red.deduction.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XmlWriter implements Writer {

     public void write(String filename, Model model) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(Model.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(model, Files.newOutputStream(Paths.get(filename)));
    }

    @Override
    public void delete(String fileName) throws SerializerException, FileNotFoundException {
        File file = new File(fileName);
        if (!file.delete()) {
            throw new SerializerException("Cannot remove file");
        }
    }
}
