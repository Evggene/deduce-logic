package red.deduction.txt;

import red.deduction.SerializerException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterWrapper {

    private BufferedWriter bufferedWriter;

    FileWriterWrapper(String filename) throws IOException {
        this.bufferedWriter = new BufferedWriter(new FileWriter(new File(filename)));
    }

    public void write(String str) throws SerializerException, IOException {
        if (bufferedWriter == null) {
            close();
        }
        bufferedWriter.write(str);
    }

    public void close() throws IOException, SerializerException {
        if (bufferedWriter == null)
            throw new SerializerException("Improper call of Serializer method (no enclosing Writer call)");
        bufferedWriter.close();
        bufferedWriter = null;
    }
}
