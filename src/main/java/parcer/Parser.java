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


public class Parser {

    enum FileState {
        RULE,
        KNOWN_FACTS,
        EOF
    }


    enum RuleState {
        AndOperator, OrOperator, Deduction, Error, BeginFact, RestFact, Transition
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

        ArrayList<Expression> orElements = new ArrayList<>();
        ArrayList<Expression> andElements = new ArrayList<>();
        RuleState ruleState = RuleState.BeginFact;
        StringBuilder fact = new StringBuilder();
        int countOperands = 0;

        for (int i = 0; i < rule.length(); i++) {

            switch (ruleState) {

                case BeginFact: {
                    ruleState = analizeFact(rule, fact, i);
                    break;
                }

                case RestFact: {
                    if (rule.charAt(i) == '-') {
                        andElements.add(new FactExpression(fact.toString()));
                        orElements.add(new AndExpression(andElements));
                        fact = new StringBuilder();
                        ruleState = RuleState.Deduction;
                        break;

                    } else if (rule.charAt(i) == '&') {
                        ruleState = RuleState.AndOperator;
                        break;

                    } else if (rule.charAt(i) == '|') {
                        ruleState = RuleState.OrOperator;
                        break;

                    } else if (rule.charAt(i) == ' ') {
                        ruleState = RuleState.Transition;
                        break;

                    } else if (!Character.isLetterOrDigit(rule.charAt(i)) && rule.charAt(i) != '_') {
                        ruleState = RuleState.Error;
                        break;

                    } else {
                        fact.append(rule.charAt(i));
                        break;
                    }
                }

                case Transition: {
                    if (rule.charAt(i) == '-') {
                        andElements.add(new FactExpression(fact.toString()));
                        orElements.add(new AndExpression(andElements));
                        fact = new StringBuilder();
                        ruleState = RuleState.Deduction;
                        break;

                    } else if (rule.charAt(i) == '&') {
                        ruleState = RuleState.AndOperator;
                        break;

                    } else if (rule.charAt(i) == '|') {
                        ruleState = RuleState.OrOperator;
                        break;

                    } else if (rule.charAt(i) == ' ') {
                        break;
                    }
                }

                case AndOperator: {
                    ruleState = RuleState.BeginFact;
                    if (rule.charAt(i) == '&') {
                        andElements.add(new FactExpression(fact.toString()));
                        fact = new StringBuilder();
                        break;
                    }
                }

                case OrOperator: {
                    ruleState = RuleState.BeginFact;
                    if (rule.charAt(i) == '|') {
                        orElements.add(new FactExpression(fact.toString()));
                        fact = new StringBuilder();
                        countOperands++;
                        break;
                    }
                }

                case Deduction: {
                    if (rule.charAt(i) == '>') {
                        ruleState = RuleState.BeginFact;
                        break;
                    }
                }

                case Error:
                    throw new ParserException("invalid rule syntax");
            }
        }
        if (ruleState != RuleState.RestFact && ruleState != RuleState.Transition) {
            throw new ParserException("invalid rule syntax");
        }
        if (orElements.size() != countOperands + 1) {
            throw new ParserException("invalid rule syntax");
        }
        //    System.out.println("or " + orElements);
        return new Rule(new OrExpression(orElements), fact.toString());
    }


//    private Rule parseRule(String rule) throws ParserException {
//
//        String[] parts = rule.split("->", -1);
//        if (parts.length != 2)
//            throw new ParserException("invalid rule syntax");
//
//        return new Rule(parseOrExpression(parts[0]), validateFact(parts[1].trim()));
//    }


//    private Expression parseOrExpression(String part) throws ParserException {
//
//        String[] split = part.split("\\|\\|", -1);
//        ArrayList<Expression> orElement = new ArrayList<>();
//        for (String elem : split) {
//            orElement.add(parseAndExpression(elem));
//        }
//        return orElement.size() > 1 ? new OrExpression(orElement) : orElement.get(0);
//    }
//
//
//    private Expression parseAndExpression(String part) throws ParserException {
//
//        ArrayList<Expression> andElement = new ArrayList<>();
//        String[] split = part.split("&&", -1);
//        for (String elem : split) {
//            andElement.add(new FactExpression(validateFact(elem.trim())));
//        }
//        return andElement.size() > 1 ? new AndExpression(andElement) : new FactExpression(validateFact(part.trim()));
//    }


    private List<String> parseKnownFacts(String factsString) throws ParserException {

        List<String> knownFactsList = new ArrayList<>();
        RuleState knownFactsState = RuleState.BeginFact;
        StringBuilder fact = new StringBuilder();

        for (int i = 0; i <= factsString.length(); i++) {

            switch (knownFactsState) {
                case BeginFact: {
                    knownFactsState = analizeFact(factsString, fact, i);
                    break;
                }

                case RestFact: {
                    if (i == factsString.length()) {
                        knownFactsList.add(fact.toString().trim());
                        break;

                    } else if (factsString.charAt(i) == ' ') {
                        knownFactsState = RuleState.Transition;
                        break;

                    } else if (factsString.charAt(i) == ',') {
                        knownFactsList.add(fact.toString());
                        fact = new StringBuilder();
                        knownFactsState = RuleState.BeginFact;
                        break;

                    } else if (Character.isLetterOrDigit(factsString.charAt(i)) || factsString.charAt(i) == '_') {
                        fact.append(factsString.charAt(i));
                        break;

                    } else {
                        knownFactsState = RuleState.Error;
                        break;
                    }
                }
                case Transition: {
                    if (i == factsString.length()) {
                        knownFactsList.add(fact.toString());
                        break;

                    } else if (factsString.charAt(i) == ' ') {
                        break;

                    } else if (factsString.charAt(i) == ',') {
                        knownFactsList.add(fact.toString());
                        fact = new StringBuilder();
                        knownFactsState = RuleState.BeginFact;
                        break;

                    } else {
                        knownFactsState = RuleState.Error;
                        break;
                    }
                }
                case Error: {
                    throw new ParserException("error with known facts");
                }
            }
        }
        if (knownFactsState != RuleState.RestFact && knownFactsState != RuleState.Transition)
            throw new ParserException("error with known facts");
        if (knownFactsList.isEmpty()) throw new
                ParserException("empty known facts");

        //    System.out.println(knownFactsList);
        return knownFactsList;
    }


    private RuleState analizeFact(String rule, StringBuilder fact, int i) {

        if (rule.charAt(i) == ' ') {
            return RuleState.BeginFact;

        } else if (Character.isLetter(rule.charAt(i))) {
            fact.append(rule.charAt(i));
            return RuleState.RestFact;

        } else if (rule.charAt(i) == '_') {
            fact.append(rule.charAt(i));
            return RuleState.BeginFact;

        }
        return RuleState.Error;
    }


//    private String validateFact(String fact) throws ParserException {
//
//        boolean validFact = Pattern.compile("^_*\\p{Alpha}+\\w*$").matcher(fact).find();
//
//        if (!validFact) throw new ParserException("Wrong value " + fact);
//
//        return fact;
//    }

}