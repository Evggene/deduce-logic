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

    enum FileState {
        RULE,
        KNOWN_FACTS,
        EOF
    }


    enum RuleState {
        AndOperator, OrOperator, BeforeOperator, Deduction,
        BeforeFact, Fact, UnderscoreFact,
        BeforeResultFact, UnderscoreResultFact, ResultFact, EOL,
        StartBracketExpression, EndBracketExpression,
        InBracketFact, InBracketBeforeOperator, InBracketAndOperator, InBracketOrOperator, InBracketBeforeFact, InBracketsUnderscoreFact
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


    private Rule parseRule(String rule) throws ParserException {

        ArrayList<Expression> orElements = new ArrayList<>();
        ArrayList<Expression> andElements = new ArrayList<>();
        RuleState ruleState = RuleState.BeforeFact;
        StringBuilder fact = new StringBuilder();
        Expression currentExpression = null;
        ArrayDeque<String> s = new ArrayDeque<>();
        ArrayList<Expression> subOrElements = new ArrayList<>();
        ArrayList<Expression> subAndElements = new ArrayList<>();
        Expression currentBrackExpr = null;
        ArrayDeque<Expression> bracketDeque = new ArrayDeque<>();


        for (int i = 0; i < rule.length(); i++) {

            switch (ruleState) {

                case BeforeFact:
                    if (rule.charAt(i) == ' ')
                        break;
                    if (Character.isLetter(rule.charAt(i))) {
                        fact.append(rule.charAt(i));
                        ruleState = RuleState.Fact;
                        break;
                    }
                    if (rule.charAt(i) == '_') {
                        fact.append(rule.charAt(i));
                        ruleState = RuleState.UnderscoreFact;
                        break;
                    }
                    if (rule.charAt(i) == '(') {
                        s.add("(");
                        ruleState = RuleState.StartBracketExpression;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case InBracketBeforeFact:
                    if (rule.charAt(i) == ' ')
                        break;
                    if (Character.isLetter(rule.charAt(i))) {
                        fact.append(rule.charAt(i));
                        ruleState = RuleState.InBracketFact;
                        break;
                    }
                    if (rule.charAt(i) == '_') {
                        fact.append(rule.charAt(i));
                        ruleState = RuleState.InBracketsUnderscoreFact;
                        break;
                    }
                    if (rule.charAt(i) == '(') {
                        s.add("(");
                        ruleState = RuleState.StartBracketExpression;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case InBracketsUnderscoreFact:
                    if (rule.charAt(i) == '_') {
                        fact.append(rule.charAt(i));
                        break;
                    }
                    if (Character.isLetter(rule.charAt(i))) {
                        fact.append(rule.charAt(i));
                        ruleState = RuleState.InBracketFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case StartBracketExpression: {

                    if (rule.charAt(i) == ' ')
                        break;
                    if (currentBrackExpr != null) {
                        bracketDeque.add(currentBrackExpr);
                        if (!subAndElements.isEmpty()) {
                            subAndElements = new ArrayList<>();
                        }
                        if (!subOrElements.isEmpty()) {
                            subOrElements = new ArrayList<>();
                        }
                    }
                    if (rule.charAt(i) == '_') {
                        fact.append(rule.charAt(i));
                        ruleState = RuleState.UnderscoreFact;
                        break;
                    }
                    if (Character.isLetter(rule.charAt(i))) {
                        fact.append(rule.charAt(i));
                        ruleState = RuleState.InBracketFact;
                        break;
                    }
                    if (rule.charAt(i) == '(') {
                        s.add("(");
                        // subOrElements = new ArrayList<>();
                        // subAndElements = new ArrayList<>();
                        break;
                    }
                    throw new ParserException("invalid rule syntax");
                }


                case UnderscoreFact:
                    if (rule.charAt(i) == '_') {
                        fact.append(rule.charAt(i));
                        break;
                    }
                    if (Character.isLetter(rule.charAt(i))) {
                        fact.append(rule.charAt(i));
                        ruleState = RuleState.Fact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case Fact:
                    if (rule.charAt(i) == '-') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        ruleState = RuleState.Deduction;
                        break;
                    }
                    if (rule.charAt(i) == '&') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        ruleState = RuleState.AndOperator;
                        break;
                    }
                    if (rule.charAt(i) == '|') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        ruleState = RuleState.OrOperator;
                        break;
                    }
                    if (rule.charAt(i) == ' ') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        ruleState = RuleState.BeforeOperator;
                        break;
                    }
                    if (rule.charAt(i) == ')') {
                        currentExpression = new FactExpression(fact.toString());
                        fact = new StringBuilder();

                        if ("(" != s.poll()) {
                            throw new ParserException("invalid brackets");
                        }
                        ruleState = RuleState.EndBracketExpression;
                        break;
                    }
                    if (!Character.isLetterOrDigit(rule.charAt(i)) && rule.charAt(i) != '_') {
                        throw new ParserException("invalid rule syntax");
                    }
                    fact.append(rule.charAt(i));
                    break;


                case InBracketFact:
                    if (rule.charAt(i) == '&') {
                        currentBrackExpr = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        ruleState = RuleState.InBracketAndOperator;
                        break;
                    }
                    if (rule.charAt(i) == '|') {
                        currentBrackExpr = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        ruleState = RuleState.InBracketOrOperator;
                        break;
                    }
                    if (rule.charAt(i) == ' ') {
                        currentBrackExpr = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        ruleState = RuleState.InBracketBeforeOperator;
                        break;
                    }
                    if (rule.charAt(i) == ')') {
                        if ("(" != s.poll()) {
                            throw new ParserException("invalid brackets");
                        }
                        currentBrackExpr = new FactExpression(fact.toString());
                        fact = new StringBuilder();
                        ruleState = RuleState.EndBracketExpression;
                        break;
                    }
                    if (!Character.isLetterOrDigit(rule.charAt(i)) && rule.charAt(i) != '_') {
                        throw new ParserException("invalid rule syntax");
                    }
                    fact.append(rule.charAt(i));
                    break;


                case BeforeOperator:
                    if (rule.charAt(i) == '-') {
                        ruleState = RuleState.Deduction;
                        break;
                    }
                    if (rule.charAt(i) == '&') {
                        ruleState = RuleState.AndOperator;
                        break;
                    }
                    if (rule.charAt(i) == '|') {
                        ruleState = RuleState.OrOperator;
                        break;
                    }
                    if (rule.charAt(i) == ' ') {
                        break;
                    }
                    if (rule.charAt(i) == ')') {
                        if ("(" != s.poll()) {
                            throw new ParserException("invalid brackets");
                        }
                        ruleState = RuleState.EndBracketExpression;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case InBracketBeforeOperator:
                    if (rule.charAt(i) == '&') {
                        ruleState = RuleState.InBracketAndOperator;
                        break;
                    }
                    if (rule.charAt(i) == '|') {
                        ruleState = RuleState.InBracketOrOperator;
                        break;
                    }
                    if (rule.charAt(i) == ' ') {
                        break;
                    }
                    if (rule.charAt(i) == ')') {
                        if ("(" != s.poll()) {
                            throw new ParserException("invalid brackets");
                        }
                        ruleState = RuleState.EndBracketExpression;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case EndBracketExpression: {


                    if (!subAndElements.isEmpty()) {
                        subAndElements.add(currentBrackExpr);
                        currentBrackExpr = new AndExpression(subAndElements);
                        subAndElements = new ArrayList<>();
                    }
                    if (!subOrElements.isEmpty()) {
                        subOrElements.add(currentBrackExpr);
                        currentBrackExpr = new OrExpression(subOrElements);
                        subOrElements = new ArrayList<>();
                    }
        //                          System.out.println(bracketDeque.peek());
                    while (!bracketDeque.isEmpty()) {
                        //                System.out.println("stack " + bracketDeque.peek());
                        Expression e = bracketDeque.pollLast();
                        subOrElements.add(e);
                     //   System.out.println(currentBrackExpr);
                        subOrElements.add(currentBrackExpr);

                        if ((e instanceof FactExpression)) {                    // плохой участок
                            currentBrackExpr = new OrExpression(subOrElements);

                        }

                        //           System.out.println(subOrElements);
                        subAndElements = new ArrayList<>();
                        subOrElements = new ArrayList<>();
                    }

                    System.out.println(currentBrackExpr);
                    if (s.isEmpty()) {
                        currentExpression = currentBrackExpr;
                        //              System.out.println(currentExpression);
                    }

                    if (rule.charAt(i) == ' ') {
                        break;
                    }
                    if (rule.charAt(i) == ')') {
                        if ("(" != s.poll()) {
                            throw new ParserException("invalid brackets");
                        }
                        break;
                    }
                    if (!s.isEmpty()) {
                        if (rule.charAt(i) == '&') {
                            ruleState = RuleState.InBracketAndOperator;
                            break;
                        }
                        if (rule.charAt(i) == '|') {
                            ruleState = RuleState.InBracketOrOperator;
                            break;
                        }
                    }
                    if (rule.charAt(i) == '-') {
                        if (subAndElements.isEmpty() && subOrElements.isEmpty()) {
                            ruleState = RuleState.Deduction;
                            break;
                        } else {
                            ruleState = RuleState.BeforeResultFact;
                            break;
                        }
                    }
                    if (rule.charAt(i) == '&') {
                        ruleState = RuleState.AndOperator;
                        break;
                    }
                    if (rule.charAt(i) == '|') {
                        ruleState = RuleState.OrOperator;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");
                }


                case AndOperator:
                    if (rule.charAt(i) == '&') {
                        andElements.add(currentExpression);
                        ruleState = RuleState.BeforeFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case OrOperator:
                    if (rule.charAt(i) == '|') {
                        if (!andElements.isEmpty()) {
                            andElements.add(currentExpression);
                            currentExpression = new AndExpression(andElements);
                            andElements = new ArrayList<>();
                        }
                        orElements.add(currentExpression);
                        ruleState = RuleState.BeforeFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case InBracketAndOperator:
                    if (rule.charAt(i) == '&') {
                        //System.out.println(bracketDeque);
                        subAndElements.add(currentBrackExpr);
                        //currentBrackExpr = null;
                        ruleState = RuleState.InBracketBeforeFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case InBracketOrOperator:
                    if (rule.charAt(i) == '|') {
                        if (!subAndElements.isEmpty()) {
                            subAndElements.add(currentBrackExpr);
                            currentBrackExpr = new AndExpression(subAndElements);

                            subAndElements = new ArrayList<>();
                        }

                        subOrElements.add(currentBrackExpr);
                        //currentBrackExpr = null;
                        ruleState = RuleState.InBracketBeforeFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case Deduction:
                    if (rule.charAt(i) == '>') {

                        if (!andElements.isEmpty()) {
                            andElements.add(currentExpression);
                            currentExpression = new AndExpression(andElements);
                            andElements = new ArrayList<>();
                        }
                        if (!orElements.isEmpty()) {
                            orElements.add(currentExpression);
                            currentExpression = new OrExpression(orElements);
                        }

                        //            System.out.println("! " + currentExpression);


                        ruleState = RuleState.BeforeResultFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case BeforeResultFact:
                    if (rule.charAt(i) == '>') {
                        break;
                    }
                    if (rule.charAt(i) == ' ') {
                        break;
                    }
                    if (Character.isLetter(rule.charAt(i))) {
                        fact.append(rule.charAt(i));
                        ruleState = RuleState.ResultFact;
                        break;
                    }
                    if (rule.charAt(i) == '_') {
                        fact.append(rule.charAt(i));
                        ruleState = RuleState.UnderscoreResultFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case UnderscoreResultFact:
                    if (rule.charAt(i) == '_') {
                        fact.append(rule.charAt(i));
                        break;
                    }
                    if (Character.isLetter(rule.charAt(i))) {
                        fact.append(rule.charAt(i));
                        ruleState = RuleState.ResultFact;
                        break;
                    }
                    throw new ParserException("invalid rule syntax");


                case ResultFact:
                    if (rule.charAt(i) == ' ') {
                        ruleState = RuleState.EOL;
                        break;
                    }
                    if (!Character.isLetterOrDigit(rule.charAt(i)) && rule.charAt(i) != '_') {
                        throw new ParserException("invalid rule syntax");
                    }
                    fact.append(rule.charAt(i));
                    break;

                case EOL:
                    if (rule.charAt(i) == ' ') {
                        break;
                    }
                    throw new ParserException("invalid rule syntax");
            }
        }
        if (ruleState != RuleState.ResultFact && ruleState != RuleState.EOL) {
            throw new ParserException("invalid rule syntax");
        }
        if (!s.isEmpty()) {
            throw new ParserException("invalid brackets");
        }


//123123123
        System.out.println(currentExpression.toString());
        return new Rule(currentExpression, fact.toString());

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
        RuleState knownFactsState = RuleState.BeforeFact;
        StringBuilder fact = new StringBuilder();

        for (int i = 0; i < factsString.length(); i++) {

            switch (knownFactsState) {

                case BeforeFact:
                    if (factsString.charAt(i) == ' ') {
                        break;
                    }
                    if (Character.isLetter(factsString.charAt(i))) {
                        fact.append(factsString.charAt(i));
                        knownFactsState = RuleState.Fact;
                        break;
                    }
                    if (factsString.charAt(i) == '_') {
                        fact.append(factsString.charAt(i));
                        knownFactsState = RuleState.UnderscoreFact;
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
                        knownFactsState = RuleState.Fact;
                        break;
                    }
                    throw new ParserException("error with known facts");

                case Fact:
                    if (factsString.charAt(i) == ' ') {
                        knownFactsState = RuleState.EOL;
                        break;
                    }
                    if (factsString.charAt(i) == ',') {
                        knownFactsList.add(fact.toString());
                        fact = new StringBuilder();
                        knownFactsState = RuleState.BeforeFact;
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
                        knownFactsState = RuleState.BeforeFact;
                        break;
                    }
                    throw new ParserException("error with known facts");
            }
        }
        if (!fact.toString().trim().isEmpty())
            knownFactsList.add(fact.toString().trim());

        if (knownFactsState != RuleState.Fact && knownFactsState != RuleState.EOL)
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