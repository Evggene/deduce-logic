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

    private int currentPos = 0;

    enum FileState {
        RULE, KNOWN_FACTS, EOF
    }

    enum ExpressionState {
        AndOperator, OrOperator, BeforeOperator,
        BeforeFact, Fact, UnderscoreFact,
        StartBracketExpression, CreateExpression
    }

    enum ResultFactState {
        BeforeResultFact, UnderscoreResultFact, ResultFact, EOL, Deduction
    }

    enum KnownFactsState {
        BeforeFact, Fact, UnderscoreFact, EOL
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
                throw new ParserException("missing known facts");
            }
            if (rulesList.isEmpty()) {
                throw new ParserException("missing rules");
            }
        }
        return new Model(rulesList, resultsList);
    }


    private Expression parseExpression(String rule, int i) throws ParserException {

        ExpressionState state = ExpressionState.BeforeFact;

        ArrayList<Expression> orElements = new ArrayList<>();
        ArrayList<Expression> andElements = new ArrayList<>();
        StringBuilder fact = new StringBuilder();
        Expression currentExpression = null;
        currentPos = i;

        for (; currentPos < rule.length(); currentPos++) {
            switch (state) {

                case BeforeFact:
                    if (rule.charAt(currentPos) == ' ')
                        break;
                    if (Character.isLetter(rule.charAt(currentPos))) {
                        fact.append(rule.charAt(currentPos));
                        state = ExpressionState.Fact;
                        break;
                    }
                    if (rule.charAt(currentPos) == '_') {
                        fact.append(rule.charAt(currentPos));
                        state = ExpressionState.UnderscoreFact;
                        break;
                    }
                    if (rule.charAt(currentPos) == '(') {
                        state = ExpressionState.StartBracketExpression;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case StartBracketExpression: {
                    currentExpression = parseExpression(rule, currentPos);
                    state = ExpressionState.BeforeOperator;
                    break;
                }


                case UnderscoreFact:
                    if (rule.charAt(currentPos) == '_') {
                        fact.append(rule.charAt(currentPos));
                        break;
                    }
                    if (Character.isLetter(rule.charAt(currentPos))) {
                        fact.append(rule.charAt(currentPos));
                        state = ExpressionState.Fact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case Fact:
                    if (rule.charAt(currentPos) == '-') {
                        currentExpression = new FactExpression(fact.toString());
                        state = ExpressionState.CreateExpression;
                        break;
                    }
                    if (rule.charAt(currentPos) == '&') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        state = ExpressionState.AndOperator;
                        break;
                    }
                    if (rule.charAt(currentPos) == '|') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        state = ExpressionState.OrOperator;
                        break;
                    }
                    if (rule.charAt(currentPos) == ' ') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        state = ExpressionState.BeforeOperator;
                        break;
                    }
                    if (rule.charAt(currentPos) == ')') {
                        currentExpression = new FactExpression(fact.toString());
                        if (!andElements.isEmpty()) {
                            andElements.add(currentExpression);
                            currentExpression = new AndExpression(andElements);
                        }
                        if (!orElements.isEmpty()) {
                            orElements.add(currentExpression);
                            currentExpression = new OrExpression(orElements);
                        }
                        return currentExpression;
                    }
                    if (!Character.isLetterOrDigit(rule.charAt(currentPos)) && rule.charAt(currentPos) != '_') {
                        throw new ParserException("invalid rule syntax");
                    }
                    fact.append(rule.charAt(currentPos));
                    break;


                case CreateExpression:
                    if (!andElements.isEmpty()) {
                        andElements.add(currentExpression);
                        currentExpression = new AndExpression(andElements);
                    }
                    if (!orElements.isEmpty()) {
                        orElements.add(currentExpression);
                        currentExpression = new OrExpression(orElements);
                    }
                    return currentExpression;


                case BeforeOperator:
                    if (rule.charAt(currentPos) == '-') {
                        state = ExpressionState.CreateExpression;
                        break;
                    }
                    if (rule.charAt(currentPos) == '&') {
                        state = ExpressionState.AndOperator;
                        break;
                    }
                    if (rule.charAt(currentPos) == '|') {
                        state = ExpressionState.OrOperator;
                        break;
                    }
                    if (rule.charAt(currentPos) == ' ') {
                        break;
                    }
                    if (rule.charAt(currentPos) == ')') {
                        if (!andElements.isEmpty()) {
                            andElements.add(currentExpression);
                            currentExpression = new AndExpression(andElements);
                        }
                        if (!orElements.isEmpty()) {
                            orElements.add(currentExpression);
                            currentExpression = new OrExpression(orElements);
                        }
                        return currentExpression;
                    }
                    throw new ParserException("invalid rule syntax");


                case AndOperator:
                    if (rule.charAt(currentPos) == '&') {
                        andElements.add(currentExpression);
                        state = ExpressionState.BeforeFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case OrOperator:
                    if (rule.charAt(currentPos) == '|') {
                        if (!andElements.isEmpty()) {
                            andElements.add(currentExpression);
                            currentExpression = new AndExpression(andElements);
                            andElements = new ArrayList<>();
                        }
                        orElements.add(currentExpression);
                        state = ExpressionState.BeforeFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");
            }
        }
        throw new ParserException("invalid rule syntax");
    }


    private Rule parseRule(String rule) throws ParserException {

        currentPos = 0;
        ResultFactState state = ResultFactState.Deduction;
        Expression resultExpression = parseExpression(rule, currentPos);
        StringBuilder fact = new StringBuilder();

        for (; currentPos < rule.length(); currentPos++) {

            switch (state) {

                case Deduction:
                    if (rule.charAt(currentPos) == '>') {
                        state = ResultFactState.BeforeResultFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");

                case BeforeResultFact:
                    if (rule.charAt(currentPos) == '>') {
                        break;
                    }
                    if (rule.charAt(currentPos) == ' ') {
                        break;
                    }
                    if (Character.isLetter(rule.charAt(currentPos))) {
                        fact.append(rule.charAt(currentPos));
                        state = ResultFactState.ResultFact;
                        break;
                    }
                    if (rule.charAt(currentPos) == '_') {
                        fact.append(rule.charAt(currentPos));
                        state = ResultFactState.UnderscoreResultFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case UnderscoreResultFact:
                    if (rule.charAt(currentPos) == '_') {
                        fact.append(rule.charAt(currentPos));
                        break;
                    }
                    if (Character.isLetter(rule.charAt(currentPos))) {
                        fact.append(rule.charAt(currentPos));
                        state = ResultFactState.ResultFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case ResultFact:
                    if (rule.charAt(currentPos) == ' ') {
                        state = ResultFactState.EOL;
                        break;
                    }
                    if (!Character.isLetterOrDigit(rule.charAt(currentPos)) && rule.charAt(currentPos) != '_') {
                        throw new ParserException("invalid rule syntax");
                    }
                    fact.append(rule.charAt(currentPos));
                    break;


                case EOL:
                    if (rule.charAt(currentPos) == ' ') {
                        break;
                    }
                    throw new ParserException("invalid rule syntax");
            }
        }
        if (state != ResultFactState.ResultFact && state != ResultFactState.EOL) {
            throw new ParserException("invalid rule syntax");
        }

        //System.out.println(fact.toString());
        return new Rule(resultExpression, fact.toString());
    }


    private List<String> parseKnownFacts(String factsString) throws ParserException {

        List<String> knownFactsList = new ArrayList<>();
        KnownFactsState state = KnownFactsState.BeforeFact;
        StringBuilder fact = new StringBuilder();

        for (int i = 0; i < factsString.length(); i++) {

            switch (state) {

                case BeforeFact:
                    if (factsString.charAt(i) == ' ') {
                        break;
                    }
                    if (Character.isLetter(factsString.charAt(i))) {
                        fact.append(factsString.charAt(i));
                        state = KnownFactsState.Fact;
                        break;
                    }
                    if (factsString.charAt(i) == '_') {
                        fact.append(factsString.charAt(i));
                        state = KnownFactsState.UnderscoreFact;
                        break;
                    }
                    throw new ParserException("error with known facts");

                case UnderscoreFact:
                    if (factsString.charAt(i) == '_') {
                        fact.append(factsString.charAt(i));
                        break;
                    }
                    if (Character.isLetter(factsString.charAt(i))) {
                        fact.append(factsString.charAt(i));
                        state = KnownFactsState.Fact;
                        break;
                    }
                    throw new ParserException("error with known facts");

                case Fact:
                    if (factsString.charAt(i) == ' ') {
                        state = KnownFactsState.EOL;
                        break;
                    }
                    if (factsString.charAt(i) == ',') {
                        knownFactsList.add(fact.toString());
                        fact = new StringBuilder();
                        state = KnownFactsState.BeforeFact;
                        break;
                    }
                    if (Character.isLetterOrDigit(factsString.charAt(i)) || factsString.charAt(i) == '_') {
                        fact.append(factsString.charAt(i));
                        break;
                    }
                    throw new ParserException("error with known facts");


                case EOL:
                    if (factsString.charAt(i) == ' ') {
                        break;
                    }
                    if (factsString.charAt(i) == ',') {
                        knownFactsList.add(fact.toString());
                        fact = new StringBuilder();
                        state = KnownFactsState.BeforeFact;
                        break;
                    }
                    throw new ParserException("error with known facts");
            }
        }
        if (!fact.toString().trim().isEmpty())
            knownFactsList.add(fact.toString().trim());

        if (state != KnownFactsState.Fact && state != KnownFactsState.EOL)
            throw new ParserException("error with known facts");
        if (knownFactsList.isEmpty()) throw new
                ParserException("empty known facts");

        return knownFactsList;
    }


}