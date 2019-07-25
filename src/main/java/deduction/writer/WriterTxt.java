package deduction.writer;

import deduction.model.Model;
import deduction.model.Rule;
import deduction.model.expression.AndExpression;
import deduction.model.expression.Expression;
import deduction.model.expression.FactExpression;
import deduction.model.expression.OrExpression;
import deduction.parser.ParserTxt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class WriterTxt implements Writer {


    public void convert(String filename, Model model) throws IOException {
        try (FileWriter file = new FileWriter(new File(filename))) {
            StringBuilder currentLine = new StringBuilder();

            for (Rule r : model.getRulesList()) {
                currentLine.append(serializeExpression(r));
                currentLine.append(" -> ").append(r.getResultFact());
                file.write(currentLine.toString());
                file.write(System.lineSeparator());
                currentLine = new StringBuilder();
            }
            file.write(ParserTxt.separator);

            Iterator<String> iterator = model.getKnownFactsList().iterator();
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
        StringBuilder sb = new StringBuilder();
        if (rule.getExpression().getStringPresentation().equals("Fact")) {
            return rule.getExpression().toString();
        }
        return serializeExpression(rule.getExpression(), sb);
    }

    private String serializeExpression(Expression ex, StringBuilder sb) {

        if (ex.getStringPresentation().equals("And")) {
            StringBuilder result = new StringBuilder();
            for (Iterator<Expression> iterator = ex.getExpressions().iterator(); iterator.hasNext(); ) {
                Expression expression = iterator.next();
                if (expression.getStringPresentation().equals("Or")) {
                    result.append("(");
                    serializeExpression(expression, sb.append(result));
                    sb.append(")");
                    result = new StringBuilder();
                    break;
                }
                if (expression.getStringPresentation().equals("And")) {
                    result.append(serializeExpression(expression, sb.append(result)));
                    result = new StringBuilder();
                    break;
                } else {
                    result.append(expression);
                }
                if (!iterator.hasNext()) {
                    break;
                }
                result.append(" && ");
            }
            sb.append(result);
        }
        if (ex.getStringPresentation().equals("Or")) {
            StringBuilder result = new StringBuilder();
            for (Iterator<Expression> iterator = ex.getExpressions().iterator(); iterator.hasNext(); ) {
                Expression expression = iterator.next();
                if (!expression.getStringPresentation().equals("Fact")) {
                    result.append(serializeExpression(expression, sb.append(result)));
                    result = new StringBuilder();
                    break;
                } else {
                    result.append(expression);
                }
                if (!iterator.hasNext()) {
                    break;
                }
                result.append(" || ");
            }
            sb.append(result);
        }
        return sb.toString();
    }

}
