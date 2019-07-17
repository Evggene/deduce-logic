package conversion;

import model.Model;
import model.Rule;
import model.expression.AndExpression;
import model.expression.Expression;
import model.expression.OrExpression;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class ConversionInTxt implements Conversion {

    private Model model;

    public ConversionInTxt(Model model) {
        this.model = model;
    }

    public void convert(String filePath) throws IOException {
        try (FileWriter file = new FileWriter(new File(filePath))) {
            StringBuilder currentLine = new StringBuilder();
            for (Iterator<Rule> iterator = model.getRulesList().iterator(); iterator.hasNext(); ) {
                Rule r = iterator.next();

                currentLine.append(convertExpression(r));

                currentLine.append(" -> " + r.getResultFact());
                file.write(currentLine.toString());
                currentLine = new StringBuilder();


                if (!iterator.hasNext()) {
                    file.write(System.lineSeparator());
                    file.write("----------------------------------------------------------------");
                    break;
                }
                file.write(System.lineSeparator());
            }

            for (Iterator<String> iterator = model.getKnownFactsList().iterator(); iterator.hasNext(); ) {
                String s = iterator.next();
                currentLine.append(s);
                if (!iterator.hasNext()) {
                    break;
                }
                currentLine.append(", ");
            }
            file.write(System.lineSeparator());
            file.write(currentLine.toString());
        }

    }


    String convertExpression(Rule rule) {
        StringBuilder sb = new StringBuilder();
        return recursiveConvertExpression(rule.getExpression(), sb);
    }

    private String recursiveConvertExpression(Expression ex, StringBuilder sb) {

        if (ex instanceof AndExpression) {
            StringBuilder result = new StringBuilder();
            for (Iterator<Expression> iterator = ex.getExpressions().iterator(); iterator.hasNext(); ) {
                Expression expression = iterator.next();
                if (expression instanceof OrExpression) {
                    result.append("(");
                    recursiveConvertExpression(expression, sb.append(result));
                    sb.append(")");
                    result = new StringBuilder();
                    break;
                }
                if (expression instanceof AndExpression) {
                    result.append(recursiveConvertExpression(expression, sb.append(result)));
                    result = new StringBuilder();
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
        if (ex instanceof OrExpression) {
            StringBuilder result = new StringBuilder();
            for (Iterator<Expression> iterator = ex.getExpressions().iterator(); iterator.hasNext(); ) {
                Expression expression = iterator.next();
                if (expression instanceof AndExpression) {
                    result.append(recursiveConvertExpression(expression, sb.append(result)));
                    result = new StringBuilder();
                    break;
                }
                if (expression instanceof OrExpression) {
                    result.append(recursiveConvertExpression(expression, sb.append(result)));
                    result = new StringBuilder();
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
