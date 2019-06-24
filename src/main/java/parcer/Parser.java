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
        Set<String> resultsList = new HashSet<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(path), Charset.forName("UTF-8"))) {
            String separator = "----------------------------------------------------------------";
            String readLine;

            FileState fileState = FileState.RULE;

            while ((readLine = br.readLine()) != null) {
                switch (fileState) {

                    case RULE:
                        if (readLine.equals(separator)) {
                            fileState = FileState.KNOWN_FACTS;
                            break;
                        }

                        rulesList.add(parseRule(readLine));
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

        String[] split = part.split("\\|\\|");
        ArrayList<Expression> orElement = new ArrayList<>();

        for (String elem : split) {
            orElement.add(parseAndExpression(elem));
        }
        return orElement.size() > 1 ? new OrExpression(orElement) : orElement.get(0);
    }



    private Expression parseAndExpression(String part) throws ParserException {

        ArrayList<Expression> andElement = new ArrayList<>();
        String[] split = part.split(Operators.AND.getSymbol());

        for (String elem : split) {
            andElement.add(new FactExpression(validateFact(elem.trim())));
        }
        return andElement.size() > 1 ? new AndExpression(andElement) : new FactExpression(validateFact(part.trim()));
    }


    private List<String> parseKnownFacts(String factsLine) throws ParserException {

        String[] knowingFactsLine = factsLine.split(",");
        for (int k = 0; k < knowingFactsLine.length; k++) {
            knowingFactsLine[k] = validateFact(knowingFactsLine[k].trim());
        }
        return Arrays.asList(knowingFactsLine);
    }


    private String validateFact(String fact) throws ParserException {

//        boolean isWrongSymbol = Pattern.compile("^\\p{Digit}|[_][\\p{Digit}]|[^\\w]").matcher(fact).find();
//        boolean isLetter = Pattern.compile("[\\p{Alpha}]").matcher(fact).find();


            boolean begin = Pattern.compile("^[_\\p{Alpha}]").matcher(fact).find();
            boolean isLetter = Pattern.compile("[\\p{Alpha}]").matcher(fact).find();
            boolean otherSymbol = Pattern.compile("[^\\w]").matcher(fact).find();



            if (otherSymbol || !isLetter || !begin)
                throw new ParserException("Wrong value " + fact);

        return fact;
    }

}