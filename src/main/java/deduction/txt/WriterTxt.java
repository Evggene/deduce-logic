package deduction.txt;



import deduction.model.Model;
import deduction.model.Rule;
import deduction.model.Expression;
import deduction.Writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class WriterTxt implements Writer {


    public void write(String filename, Model model) throws IOException {
        try (FileWriter file = new FileWriter(new File(filename))) {
            StringBuilder currentLine = new StringBuilder();

            for (Rule rule : model.getRules()) {
                currentLine.append(serializeExpression(rule));
                currentLine.append(" -> ").append(rule.getResultFact());
                file.write(currentLine.toString());
                file.write(System.lineSeparator());
                currentLine = new StringBuilder();
            }
            file.write(ParserTxt.separator);

            Iterator<String> iterator = model.getKnownFacts().iterator();
            if (iterator.hasNext())
                currentLine.append(iterator.next());
            while (iterator.hasNext()) {
                currentLine.append(", ").append(iterator.next());
            }
            file.write(System.lineSeparator());
            file.write(currentLine.toString());
        }
    }

    private String serializeExpression(Rule rule) {
        StringBuilder result = new StringBuilder();
        Expression ex = rule.getExpression();
        if (ex.getClass().getSimpleName().equals("FactExpression")) {
            result.append(ex.toString());
        }
        if (ex.getClass().getSimpleName().equals("AndExpression")) {
            result = serializeExpression(ex, result, " && ");
        }
        if (ex.getClass().getSimpleName().equals("OrExpression")) {
            result = serializeExpression(ex, result, " || ");
        }
        return result.toString();
    }

    private StringBuilder serializeExpression(Expression ex, StringBuilder subExpression, String func) {

        for (Iterator<Expression> iterator = ex.getExpressions().iterator(); iterator.hasNext(); ) {
            Expression expression = iterator.next();
            if (expression.getClass().getSimpleName().equals("OrExpression")) {
                subExpression.append("(");
                serializeExpression(expression, subExpression," || ");
                subExpression.append(")");
            }
            if (expression.getClass().getSimpleName().equals("AndExpression")) {
                subExpression.append(serializeExpression(expression, new StringBuilder(), " && "));
            }
            if (expression.getClass().getSimpleName().equals("FactExpression")) {
                subExpression.append(expression);
            }
            if (!iterator.hasNext()) {
                break;
            }
            subExpression.append(func);
        }
        return subExpression;
    }
}










