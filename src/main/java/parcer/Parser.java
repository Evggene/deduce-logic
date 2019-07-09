package parcer;

import com.sun.org.apache.xpath.internal.operations.Or;
import model.Model;
import model.Rule;
import model.expression.AndExpression;
import model.expression.Expression;
import model.expression.FactExpression;
import model.expression.OrExpression;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Parser {

    private int currentPos = 0;

    enum FileState {
        RULE,
        KNOWN_FACTS,
        EOF
    }

    enum ExpressionState {
        AndOperator, OrOperator, BeforeOperator, Deduction,
        BeforeFact, Fact, UnderscoreFact, StartBracketExpression
    }

    enum ResultFactState {
        BeforeResultFact, UnderscoreResultFact, ResultFact, EOL,
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

                        if (readLine == null || readLine.trim().isEmpty()) {
                            throw new ParserException("missing known facts");
                        }
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


//    class Context {
//        //Expression currentExpression = null;
//        //int i;
//    }


    private Expression rec(String rule, int i) throws ParserException {
        ArrayList<Expression> orElements = new ArrayList<>();
        ArrayList<Expression> andElements = new ArrayList<>();
        ExpressionState ruleState = ExpressionState.BeforeFact;
        StringBuilder fact = new StringBuilder();
        Expression currentExpression = null;
        //Context currentBracketExpression = new Context();

        currentPos = i;

        for (; currentPos < rule.length(); currentPos++) {
            switch (ruleState) {

                case BeforeFact:
                    if (rule.charAt(currentPos) == ' ')
                        break;
                    if (Character.isLetter(rule.charAt(currentPos))) {
                        fact.append(rule.charAt(currentPos));
                        ruleState = ExpressionState.Fact;
                        break;
                    }
                    if (rule.charAt(currentPos) == '_') {
                        fact.append(rule.charAt(currentPos));
                        ruleState = ExpressionState.UnderscoreFact;
                        break;
                    }
                    if (rule.charAt(currentPos) == '(') {
                        ruleState = ExpressionState.StartBracketExpression;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case StartBracketExpression: {
                    currentExpression = rec(rule, currentPos);
                    //currentPos = currentBracketExpression.i;
                    //currentExpression = currentBracketExpression.currentExpression;
                    ruleState = ExpressionState.BeforeOperator;
                    break;
                }


                case UnderscoreFact:
                    if (rule.charAt(currentPos) == '_') {
                        fact.append(rule.charAt(currentPos));
                        break;
                    }
                    if (Character.isLetter(rule.charAt(currentPos))) {
                        fact.append(rule.charAt(currentPos));
                        ruleState = ExpressionState.Fact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case Fact:
                    if (rule.charAt(currentPos) == '-') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        ruleState = ExpressionState.Deduction;
                        break;
                    }
                    if (rule.charAt(currentPos) == '&') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        ruleState = ExpressionState.AndOperator;
                        break;
                    }
                    if (rule.charAt(currentPos) == '|') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        ruleState = ExpressionState.OrOperator;
                        break;
                    }
                    if (rule.charAt(currentPos) == ' ') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        ruleState = ExpressionState.BeforeOperator;
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
                        //currentBracketExpression.currentExpression = currentExpression;
                        //currentBracketExpression.i = currentPos;
                        return currentExpression;
                    }
                    if (!Character.isLetterOrDigit(rule.charAt(currentPos)) && rule.charAt(currentPos) != '_') {
                        throw new ParserException("invalid rule syntax");
                    }
                    fact.append(rule.charAt(currentPos));
                    break;


                case BeforeOperator:
                    if (rule.charAt(currentPos) == '-') {
                        ruleState = ExpressionState.Deduction;
                        break;
                    }
                    if (rule.charAt(currentPos) == '&') {
                        ruleState = ExpressionState.AndOperator;
                        break;
                    }
                    if (rule.charAt(currentPos) == '|') {
                        ruleState = ExpressionState.OrOperator;
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
                        //currentBracketExpression.currentExpression = currentExpression;
                        //currentBracketExpression.i = currentPos;
                        return currentExpression;

                    }
                    throw new ParserException("invalid rule syntax");


                case AndOperator:
                    if (rule.charAt(currentPos) == '&') {
                        andElements.add(currentExpression);
                        ruleState = ExpressionState.BeforeFact;
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
                        ruleState = ExpressionState.BeforeFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case Deduction:
                    if (rule.charAt(currentPos) == '>') {

                        if (!andElements.isEmpty()) {
                            andElements.add(currentExpression);
                            currentExpression = new AndExpression(andElements);
                        }
                        if (!orElements.isEmpty()) {
                            orElements.add(currentExpression);
                            currentExpression = new OrExpression(orElements);
                        }

                        //currentBracketExpression.i = currentPos;
                        //currentBracketExpression.currentExpression = currentExpression;
                        return currentExpression;
                    }
                    throw new ParserException("invalid rule syntax");
            }
        }
        return null;
    }


    private Rule parseRule(String rule) throws ParserException {


        Expression a = rec(rule, currentPos);
        ResultFactState ruleState = ResultFactState.BeforeResultFact;
        StringBuilder fact = new StringBuilder();
                System.out.println(a);

        for (; currentPos < rule.length(); currentPos++) {
            switch (ruleState) {

                case BeforeResultFact:
                    if (rule.charAt(currentPos) == '>') {
                        break;
                    }
                    if (rule.charAt(currentPos) == ' ') {
                        break;
                    }
                    if (Character.isLetter(rule.charAt(currentPos))) {
                        fact.append(rule.charAt(currentPos));
                        ruleState = ResultFactState.ResultFact;
                        break;
                    }
                    if (rule.charAt(currentPos) == '_') {
                        fact.append(rule.charAt(currentPos));
                        ruleState = ResultFactState.UnderscoreResultFact;
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
                        ruleState = ResultFactState.ResultFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case ResultFact:
                    if (rule.charAt(currentPos) == ' ') {
                        ruleState = ResultFactState.EOL;
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
        if (ruleState != ResultFactState.ResultFact && ruleState != ResultFactState.EOL) {
            throw new ParserException("invalid rule syntax");
        }

        System.out.println(fact.toString());
        return new Rule(a, fact.toString());
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
        KnownFactsState knownFactsState = KnownFactsState.BeforeFact;
        StringBuilder fact = new StringBuilder();

        for (int i = 0; i < factsString.length(); i++) {

            switch (knownFactsState) {

                case BeforeFact:
                    if (factsString.charAt(i) == ' ') {
                        break;
                    }
                    if (Character.isLetter(factsString.charAt(i))) {
                        fact.append(factsString.charAt(i));
                        knownFactsState = KnownFactsState.Fact;
                        break;
                    }
                    if (factsString.charAt(i) == '_') {
                        fact.append(factsString.charAt(i));
                        knownFactsState = KnownFactsState.UnderscoreFact;
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
                        knownFactsState = KnownFactsState.Fact;
                        break;
                    }
                    throw new ParserException("error with known facts");

                case Fact:
                    if (factsString.charAt(i) == ' ') {
                        knownFactsState = KnownFactsState.EOL;
                        break;
                    }
                    if (factsString.charAt(i) == ',') {
                        knownFactsList.add(fact.toString());
                        fact = new StringBuilder();
                        knownFactsState = KnownFactsState.BeforeFact;
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
                        knownFactsState = KnownFactsState.BeforeFact;
                        break;
                    }
                    throw new ParserException("error with known facts");
            }
        }
        if (!fact.toString().trim().isEmpty())
            knownFactsList.add(fact.toString().trim());

        if (knownFactsState != KnownFactsState.Fact && knownFactsState != KnownFactsState.EOL)
            throw new ParserException("error with known facts");
        if (knownFactsList.isEmpty()) throw new
                ParserException("empty known facts");

        //    System.out.println(knownFactsList);
        return knownFactsList;
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