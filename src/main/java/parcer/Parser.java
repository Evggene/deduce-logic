package parcer;

import model.Model;
import model.Rule;
import model.expression.Expression;
import model.expression.FactExpression;
import model.expression.OrExpression;
import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Parser {

    public Model parse(String path) throws Exception {

        Collection<Rule> rulesList = new ArrayList<>();
        Collection<String> resultList = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(path), Charset.forName("UTF-8"))) {
            String separator = "----------------------------------------------------------------";
            String readLine = null;
            boolean isSeparator = false;

            while ((readLine = br.readLine()) != null) {
                if (!readLine.equals(separator)) {

                    Rule rule = parseRule(readLine);

                    rulesList.add(rule);


                } else {
                    resultList.addAll(parseKnownFacts(br.readLine()));
                    isSeparator = true;
                    break;
                }
            }
            if (!isSeparator) {
                throw new ParserException("missing or wrong separator");
            }
        }
        if (rulesList.isEmpty()) {
            throw new ParserException("missing rules");
        }

        return new Model(rulesList, resultList);
    }


    private List<String> parseKnownFacts(String factsLine) throws ParserException {

        if (factsLine == null || factsLine.length() == 0) {
            throw new ParserException("missing expressions");
        }

        String[] knowingFactsLine = factsLine.trim().split(",");

        for (int k = 0; k < knowingFactsLine.length; k++) {
            knowingFactsLine[k] = knowingFactsLine[k].trim();
        }
        return validate(Arrays.asList(knowingFactsLine));
    }




    private Rule parseRule(String rule) throws ParserException {

        List<String> ruleLineList = new ArrayList<>();

        if (rule.length() == 0) {
            throw new ParserException("missing rules");
        }
        if (!rule.contains("->")) {
            throw new ParserException("missing ->");
        }

        String[] split = rule.split("((?<=\\|{2})|(?=\\|{2})|(?<=&&)|(?=&&)|(?<=->)|(?=->))");




        for (int i = 0; i < split.length; i++) {
            ruleLineList.add(split[i].trim());
        }

        validate(ruleLineList);



        String deducingFact = ruleLineList.get(ruleLineList.size()-1);
        ruleLineList.remove(ruleLineList.size()-1);
        ruleLineList.remove(ruleLineList.size()-1);




        System.out.println(ruleLineList);
        Collection<Expression> e = new ArrayList<>();

        for (int i = 0; i < ruleLineList.size(); i++) {

        }



        e.add(new FactExpression(ruleLineList.get(0)));


        return new Rule(new OrExpression(e),  deducingFact);
    }




    private List<String> validate(List<String> line) throws ParserException {

        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).equals(LogicOperator.DEDUCTION.operationSymbol())
                    || line.get(i).equals(LogicOperator.AND.operationSymbol())
                    || line.get(i).equals(LogicOperator.OR.operationSymbol()))
                continue;

            if (!Character.isLetter(line.get(i).charAt(0)) && !(line.get(i).charAt(0) == '_')
                    || line.get(i).length() == 1 && !Character.isLetter(line.get(i).charAt(0))) {
                throw new ParserException("Wrong value " + line.get(i));
            }

            for (int j = 1; j < line.get(i).length(); j++) {
                if (!Character.isLetterOrDigit(line.get(i).charAt(j)) && !(line.get(i).charAt(j) == '_')) {
                    throw new ParserException("Wrong value " + line.get(i));
                }

                if (line.get(i).charAt(j - 1) == '_' && Character.isDigit(line.get(i).charAt(j))) {
                    throw new ParserException("Wrong value " + line.get(i));
                }
            }


            boolean b = false;
            for (int j = 0; j < line.get(i).length(); j++) {
                if (Character.isLetter(line.get(i).charAt(j))) {
                    b = true;
                }
            }
            if (!b)
                throw new ParserException("Wrong value " + line.get(i));
        }

        return line;
    }
}