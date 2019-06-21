package parcer;

import model.Model;
import model.Rule;
import model.expression.AndExpression;
import model.expression.Expression;
import model.expression.FactExpression;
import model.expression.OrExpression;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;


public class Parser {

    enum FileState {
        RULE,
        KNOWN_FACTS,
        EOF
    }


    public Model parse(String path) throws Exception {

        Collection<Rule> rulesList = new ArrayList<>();
        Collection<String> resultsList = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(path), Charset.forName("UTF-8"))) {
            String separator = "----------------------------------------------------------------";
            String readLine;

            FileState fileState = FileState.RULE;

            while ((readLine = br.readLine()) != null) {
                switch (fileState) {

                    case RULE:

                        if (!readLine.equals(separator))
                            rulesList.add(parseRule(readLine));
                        else
                            fileState = FileState.KNOWN_FACTS;
                        break;

                    case KNOWN_FACTS:

                        resultsList.addAll(parseKnownFacts(readLine));
                        fileState = FileState.EOF;
                        break;

                    case EOF:
                        throw new ParserException("invalid file");
                }
            }
            if (fileState == FileState.RULE) {
                throw new ParserException("missing or wrong separator");
            }
            if (fileState == FileState.KNOWN_FACTS) {
                throw new ParserException("missing facts");
            }
            if (rulesList.isEmpty()) {
                throw new ParserException("missing rules");
            }
        }

        return new Model(rulesList, resultsList);
    }


    private Rule parseRule(String rule) throws ParserException {

        String[] parts = rule.split(Operators.DEDUCTION.getSymbol());
        if (parts.length != 2)
            throw new ParserException("invalid rule syntax");

        return new Rule(parseExpression(parts[0]), validateFact(parts[1].trim()));
    }


    private Expression parseExpression(String part) throws ParserException {

        Collection<Expression> c = new ArrayList<>();
        Collection<Expression> orElement = new ArrayList<>();
        Collection<Expression> andElement = new ArrayList<>();
        Expression expression = null;

        String[] split = part.split(Operators.AND.getSymbol());

        if (split.length == 1 && !split[0].contains(Operators.OR.getSymbol())) {
            expression = new FactExpression(validateFact(split[0].trim()));
        } else {
            for (int i = 0; i < split.length; i++) {
                if (!split[i].contains(Operators.OR.getSymbol()))
                    andElement.add(new FactExpression(validateFact(split[i].trim())));
                else {
                    String[] splitOr = split[i].split("\\|{2}");
                    for (int j = 0; j < splitOr.length; j++) {
                        orElement.add(new FactExpression(validateFact(splitOr[j].trim())));
                    }
                    andElement.add(new OrExpression(orElement));
                }
                c.add(new OrExpression(orElement));
            }
            c.add(new AndExpression(andElement));
            expression = new OrExpression(c);
        }
        return expression;
    }


    private List<String> parseKnownFacts(String factsLine) throws ParserException {

        String[] knowingFactsLine = factsLine.split(",");
        for (int k = 0; k < knowingFactsLine.length; k++) {
            knowingFactsLine[k] = validateFact(knowingFactsLine[k].trim());
        }
        return Arrays.asList(knowingFactsLine);
    }


//    private Expression parseExpression(String part) throws ParserException {
//        String[] split = part.split("((?<=\\|{2})|(?=\\|{2})|(?<=&{2})|(?=&{2}))");
//        List<String> ruleList = new ArrayList<>();
//        for (int i = 0; i < split.length; i++) {
//            ruleList.add(validateFact(split[i].trim()));
//        }
//        return doExpression(ruleList);
//    }
//
//
//    private Expression doExpression(List<String> ruleList) {
//
//        Expression expression = null;
//        Collection<Expression> c = new ArrayList<>();
//        Collection<Expression> orElement = new ArrayList<>();
//        Collection<Expression> andElement = new ArrayList<>();
//
//        if (ruleList.size() == 1)
//            expression = new FactExpression(ruleList.get(0));
//        else {
//            for (int i = 1; i < ruleList.size(); i += 2) {
//                if (ruleList.get(i).equals(Operators.AND.getSymbol())) {
//                    if (!orElement.isEmpty()) {
//                        andElement.add(new OrExpression(orElement));
//                    } else {
//                        andElement.add(new FactExpression(ruleList.get(i - 1)));
//                    }
//                    andElement.add(new FactExpression(ruleList.get(i + 1)));
//                    c.add(new AndExpression(andElement));
//                } else {
//                    orElement.add(new FactExpression(ruleList.get(i - 1)));
//                    orElement.add(new FactExpression(ruleList.get(i + 1)));
//                    c.add(new OrExpression(orElement));
//                }
//                expression = new OrExpression(c);
//            }
//        }
//        return expression;
//    }


    private String validateFact(String fact) throws ParserException {

        if (!(fact.equals(Operators.AND.getSymbol()) || fact.equals(Operators.OR.getSymbol()))) {

            boolean isWrongSymbol = Pattern.compile("^[\\p{Digit}]|[_][\\p{Digit}]|__|[^\\w]").matcher(fact).find();
            boolean isLetter = Pattern.compile("[\\p{Alpha}]").matcher(fact).find();

            if (!isLetter || isWrongSymbol)
                throw new ParserException("Wrong value " + fact);
        }
        return fact;
    }

}