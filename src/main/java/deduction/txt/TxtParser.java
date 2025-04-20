package deduction.txt;

import deduction.model.*;
import deduction.parser.Parser;
import deduction.parser.ParserException;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TxtParser implements Parser {

    public static final String separator = "----------------------------------------------------------------";
    private int currentPos = 0;
    private int currentLine = 0;

    private enum FileState {
        RULE, KNOWN_FACTS, EOF
    }
    private enum ExpressionState {
        BeforeFact, UnderscoreFact, Fact,
        BeforeOperator, AndOperator, OrOperator
    }
    private enum RuleState {
        Begin, Imply,
        BeforeFact, UnderscoreFact, Fact, EOL
    }
    private enum KnownFactsState {
        BeforeFact, UnderscoreFact, Fact, EOL
    }


    public Model parse(String filename) throws Exception {

        Collection<Rule> rulesList = new ArrayList<>();
        Set<String> resultsList = new HashSet<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8)) {
            String readLine;
            FileState fileState = FileState.RULE;

            while ((readLine = br.readLine()) != null) {
                currentLine++;
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
                        throw new ParserException(currentLine, "invalid file");
                }
            }
            if (fileState == FileState.RULE) {
                throw new ParserException(currentLine, "missing or wrong separator");
            }
            if (fileState == FileState.KNOWN_FACTS) {
                throw new ParserException(currentLine, "missing known facts");
            }
            if (rulesList.isEmpty()) {
                throw new ParserException(currentLine, "missing rules");
            }
        }
        return new Model(rulesList, resultsList);
    }

    private Expression parseExpression(String rule) throws ParserException {
        ExpressionState state = ExpressionState.BeforeFact;
        ArrayList<Expression> orElements = new ArrayList<>();
        ArrayList<Expression> andElements = new ArrayList<>();
        StringBuilder fact = new StringBuilder();
        Expression currentExpression = null;

        for (currentPos++; currentPos < rule.length(); currentPos++) {
            char currentChar = rule.charAt(currentPos);
            switch (state) {
                case BeforeFact:
                    if (currentChar == ' ')
                        break;
                    if (Character.isLetter(currentChar)) {
                        fact.append(currentChar);
                        state = ExpressionState.Fact;
                        break;
                    }
                    if (currentChar == '_') {
                        fact.append(currentChar);
                        state = ExpressionState.UnderscoreFact;
                        break;
                    }
                    if (currentChar == '(') {
                        currentExpression = parseExpression(rule);
                        state = ExpressionState.BeforeOperator;
                        break;
                    }
                    throw new ParserException(currentLine, "invalid rule syntax");

                case UnderscoreFact:
                    if (currentChar == '_') {
                        fact.append(currentChar);
                        break;
                    }
                    if (Character.isLetter(currentChar)) {
                        fact.append(currentChar);
                        state = ExpressionState.Fact;
                        break;
                    }
                    throw new ParserException(currentLine, "invalid rule syntax (wrong symbols)");

                case Fact:
                    if (currentChar == '-') {
                        currentExpression = new FactExpression(fact.toString());
                        return assembleExpression(orElements, andElements, currentExpression);
                    }
                    if (currentChar == '&') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        state = ExpressionState.AndOperator;
                        break;
                    }
                    if (currentChar == '|') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        state = ExpressionState.OrOperator;
                        break;
                    }
                    if (currentChar == ' ') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        state = ExpressionState.BeforeOperator;
                        break;
                    }
                    if (currentChar == ')') {
                        currentExpression = new FactExpression(fact.toString());
                        return assembleExpression(orElements, andElements, currentExpression);
                    }
                    if (!Character.isLetterOrDigit(currentChar) && currentChar != '_') {
                        throw new ParserException(currentLine, "invalid rule syntax (wrong symbols)");
                    }
                    fact.append(currentChar);
                    break;

                case BeforeOperator:
                    if (currentChar == '-') {
                        return assembleExpression(orElements, andElements, currentExpression);
                    }
                    if (currentChar == '&') {
                        state = ExpressionState.AndOperator;
                        break;
                    }
                    if (currentChar == '|') {
                        state = ExpressionState.OrOperator;
                        break;
                    }
                    if (currentChar == ' ') {
                        break;
                    }
                    if (currentChar == ')') {
                        return assembleExpression(orElements, andElements, currentExpression);
                    }
                    throw new ParserException(currentLine, "invalid rule syntax");

                case AndOperator:
                    if (currentChar == '&') {
                        andElements.add(currentExpression);
                        state = ExpressionState.BeforeFact;
                        break;
                    }
                    throw new ParserException(currentLine, "invalid rule syntax (wrong operator)");

                case OrOperator:
                    if (currentChar == '|') {
                        if (!andElements.isEmpty()) {
                            andElements.add(currentExpression);
                            currentExpression = new AndExpression(andElements);
                            andElements = new ArrayList<>();
                        }
                        orElements.add(currentExpression);
                        state = ExpressionState.BeforeFact;
                        break;
                    }
                    throw new ParserException(currentLine, "invalid rule syntax (wrong operator)");
            }
        }
        throw new ParserException(currentLine, "invalid rule syntax)");
    }

    private Expression assembleExpression(ArrayList<Expression> orElements, ArrayList<Expression> andElements, Expression currentExpression) {
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


    private Rule parseRule(String rule) throws ParserException {

        currentPos = -1;
        Expression resultExpression = parseExpression(rule);
        RuleState state = RuleState.Begin;
        StringBuilder fact = new StringBuilder();

        for (; currentPos < rule.length(); currentPos++) {
            char currentChar = rule.charAt(currentPos);
            switch (state) {

                case Begin:
                    if (currentChar == '-') {
                        state = RuleState.Imply;
                        break;
                    }
                    throw new ParserException(currentLine, "invalid rule syntax");

                case Imply:
                    if (currentChar == '>') {
                        state = RuleState.BeforeFact;
                        break;
                    }
                    throw new ParserException(currentLine, "invalid rule syntax");

                case BeforeFact:
                    if (currentChar == ' ') {
                        break;
                    }
                    if (Character.isLetter(currentChar)) {
                        fact.append(currentChar);
                        state = RuleState.Fact;
                        break;
                    }
                    if (currentChar == '_') {
                        fact.append(currentChar);
                        state = RuleState.UnderscoreFact;
                        break;
                    }
                    throw new ParserException(currentLine, "invalid rule syntax (wrong symbol in deducing fact)");

                case UnderscoreFact:
                    if (currentChar == '_') {
                        fact.append(currentChar);
                        break;
                    }
                    if (Character.isLetter(currentChar)) {
                        fact.append(currentChar);
                        state = RuleState.Fact;
                        break;
                    }
                    throw new ParserException(currentLine, "invalid rule syntax (wrong symbol in deducing fact)");

                case Fact:
                    if (currentChar == ' ') {
                        state = RuleState.EOL;
                        break;
                    }
                    if (!Character.isLetterOrDigit(currentChar) && currentChar != '_') {
                        throw new ParserException(currentLine, "invalid rule syntax (wrong symbol in deducing fact)");
                    }
                    fact.append(currentChar);
                    break;

                case EOL:
                    if (currentChar == ' ') {
                        break;
                    }
                    throw new ParserException(currentLine, "invalid rule syntax (error in end of rule)");
            }
        }
        if (state != RuleState.Fact && state != RuleState.EOL) {
            throw new ParserException(currentLine, "invalid rule syntax");
        }
// PRINT
        //    System.out.println("parser txt " + resultExpression);
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
                    throw new ParserException(currentLine, "error with known facts");

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
                    throw new ParserException(currentLine, "error with known facts");

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
                    throw new ParserException(currentLine, "error with known facts");

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
                    throw new ParserException(currentLine, "error with known facts");
            }
        }
        if (!fact.toString().trim().isEmpty())
            knownFactsList.add(fact.toString().trim());
        if (state != KnownFactsState.Fact && state != KnownFactsState.EOL)
            throw new ParserException(currentLine, "error with known facts");
        if (knownFactsList.isEmpty()) throw new
                ParserException(currentLine, "empty known facts");

        return knownFactsList;
    }
}