

import sun.rmi.runtime.Log;

import java.util.*;
import java.util.regex.Pattern;


public class Parser {

    public List<String> parseFactsLine(String factsLine) throws ParserException {

        if (factsLine == null || factsLine.length() == 0) {
            throw new ParserException("missing facts");
        }

        String[] knowingFactsLine = factsLine.trim().split(",");

        for (int k = 0; k < knowingFactsLine.length; k++) {
            knowingFactsLine[k] = knowingFactsLine[k].trim();
        }
        return validateLine(Arrays.asList(knowingFactsLine));
    }


    public List<String> parseRulesLine(String ruleLine) throws ParserException {

        List<String> ruleLineList = new ArrayList<>();

        if (ruleLine.length() == 0) {
            throw new ParserException("missing rules");
        }
        if (!ruleLine.contains("->")) {
            throw new ParserException("missing ->");
        }

        StringBuilder regex = new StringBuilder();
        regex.append("((?<=\\|{2})|(?=\\|{2})");
        for (int i = 0; i < LogicOperations.values().length; i++) {
            if (LogicOperations.values()[i].operationSymbol() == "||") continue;
            regex.append("|(?<=").append(LogicOperations.values()[i].operationSymbol()).append(")")
                    .append("|(?=").append(LogicOperations.values()[i].operationSymbol()).append(")");
        }
        regex.append(")");

        String[] split = ruleLine.split(regex.toString());

        for (int i = 0; i < split.length; i++) {
            ruleLineList.add(split[i].trim());
        }

        return validateLine(ruleLineList);
    }


    private List<String> validateLine(List<String> line) throws ParserException {

        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).equals("||") || line.get(i).equals("&&") || line.get(i).equals("->"))
                continue;

            if (!Character.isLetter(line.get(i).charAt(0)) && !(line.get(i).charAt(0) == '_')
                    || line.get(i).length() == 1 && !Character.isLetter(line.get(i).charAt(0))) {
                throw new ParserException("Wrong value " + line.get(i));
            }

            for (int j = 1; j < line.get(i).length(); j++) {
                if (!Character.isLetterOrDigit(line.get(i).charAt(j)) && line.get(i).charAt(j) != '_') {
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