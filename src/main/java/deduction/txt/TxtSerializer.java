package deduction.txt;


import deduction.model.*;
import deduction.Writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


public class TxtSerializer implements Writer, Serializer {

    private StringBuilder line;
    private int andDepth;


    public void write(String filename, Model model) throws IOException {
        line = new StringBuilder();
        model.serialize(this);
        try (FileWriter file = new FileWriter(new File(filename))) {
            file.write(line.toString());
        }
        line = null;
    }

    @Override
    public void serializeModel(Collection<Rule> rulesList, Set<String> knownFactsList) {
        for (Rule rule : rulesList) {
            rule.serialize(this);
        }
        line.append(TxtParser.separator);
        line.append(System.lineSeparator());

        Iterator<String> iterator = knownFactsList.iterator();
        line.append(iterator.next());
        while (iterator.hasNext()) {
            line.append(", ").append(iterator.next());
        }
    }

    @Override
    public void serializeRule(Expression expression, String resultFact) {
        expression.serialize(this);
        line.append(" -> ");
        line.append(resultFact);
        line.append(System.lineSeparator());
    }

    @Override
    public void serializeAndExpression(Collection<Expression> expressions) {
        andDepth++;
        Iterator<Expression> iterator = expressions.iterator();
        iterator.next().serialize(this);
        while (iterator.hasNext()) {
            line.append(" && ");
            iterator.next().serialize(this);
        }
        andDepth--;
    }

    @Override
    public void serializeOrExpression(Collection<Expression> expressions) {
        Iterator<Expression> iterator = expressions.iterator();
        if (andDepth > 0)
            line.append("(");
        iterator.next().serialize(this);
        while (iterator.hasNext()) {
            line.append(" || ");
            iterator.next().serialize(this);
        }
        if (andDepth > 0)
            line.append(")");
    }

    @Override
    public void serializeFactExpression(String fact) {
        line.append(fact);
    }
}










