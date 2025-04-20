package red.deduction.txt;

import red.deduction.SerializerException;
import red.deduction.model.*;
import red.deduction.Writer;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class TxtWriter implements Writer, Serializer {

    private int andDepth;
    private FileWriterWrapper file = null;

    public void write(String filename, Model model) throws IOException, SerializerException {
        try {
            file = new FileWriterWrapper(filename);
            model.serialize(this);
        } finally {
            file.close();
        }
    }

    @Override
    public void delete(String fileName) throws SerializerException, FileNotFoundException {
        File file = new File(fileName);
        if (!file.delete()) {
            throw new SerializerException("Cannot remove file");
        }
    }

    @Override
    public void serializeModel(Collection<Rule> rulesList, Set<String> knownFactsList) throws SerializerException, IOException {
        for (Rule rule : rulesList) {
            rule.serialize(this);
        }
        file.write(TxtParser.separator);
        file.write(System.lineSeparator());

        Iterator<String> iterator = knownFactsList.iterator();
        file.write(iterator.next());
        while (iterator.hasNext()) {
            file.write(", ");
            file.write(iterator.next());
        }
    }

    @Override
    public void serializeRule(Expression expression, String resultFact) throws IOException, SerializerException {
        expression.serialize(this);
        file.write(" -> ");
        file.write(resultFact);
        file.write(System.lineSeparator());
    }

    @Override
    public void serializeAndExpression(Collection<Expression> expressions) throws IOException, SerializerException {
        andDepth++;
        Iterator<Expression> iterator = expressions.iterator();
        iterator.next().serialize(this);
        while (iterator.hasNext()) {
            file.write(" && ");
            iterator.next().serialize(this);
        }
        andDepth--;
    }

    @Override
    public void serializeOrExpression(Collection<Expression> expressions) throws IOException, SerializerException {
        Iterator<Expression> iterator = expressions.iterator();
        if (andDepth > 0)
            file.write("(");
        iterator.next().serialize(this);
        while (iterator.hasNext()) {
            file.write(" || ");
            iterator.next().serialize(this);
        }
        if (andDepth > 0)
            file.write(")");
    }

    @Override
    public void serializeFactExpression(String fact) throws IOException, SerializerException {
        file.write(fact);
    }
}



