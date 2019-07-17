package model;

import model.Model;
import model.Rule;
import model.expression.AndExpression;
import model.expression.Expression;
import model.expression.FactExpression;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class Conversion {

    private Model model;

    public Conversion(Model model) {
        this.model = model;
    }

    public void convertXmlToTxt(String filePath) throws IOException {
                try (FileWriter fr = new FileWriter(new File(filePath))) {
            StringBuilder sb = new StringBuilder();
            for (Iterator<Rule> iterator = model.getRulesList().iterator(); iterator.hasNext(); ) {
                Rule r = iterator.next();
                sb.append(r.getExpression().toString());

                sb.append(" -> " + r.getResultFact());
                fr.write(sb.toString());
                sb = new StringBuilder();

                if (!iterator.hasNext()) {
                    fr.write(System.lineSeparator());
                    fr.write("----------------------------------------------------------------");
                    break;
                }
            }

            for (Iterator<String> iterator = model.getKnownFactsList().iterator(); iterator.hasNext(); ) {
                String s = iterator.next();
                sb.append(s);
                if (!iterator.hasNext()) {
                    break;
                }
                sb.append(", ");
            }
            fr.write(System.lineSeparator());
            fr.write(sb.toString());
        }
    }




}
