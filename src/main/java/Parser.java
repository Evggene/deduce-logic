
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Parser {
    private List<List<String>> rulesList;
    private List<String> resultList;


    public Parser(List<List<String>> rulesList, List<String> resultList) {
        this.rulesList = rulesList;
        this.resultList = resultList;
    }

    public void parse(String path) throws Exception {

        try (BufferedReader br = Files.newBufferedReader(Paths.get(path), Charset.forName("UTF-8"))) {
            String separator = "----------------------------------------------------------------";
            String readLine = null;
            boolean isSeparator = false;

            while ((readLine = br.readLine()) != null) {
                if (!readLine.startsWith("--------------------")) {
                    rulesList.add(validateLine(parseRulesLine(readLine)));
                } else {
                    if (readLine.equals(separator)) {
                        resultList.addAll(validateLine(parseFactsLine(br.readLine())));
                        isSeparator = true;
                        break;
                    } else {
                        throw new ParserException("missing or wrong separator");
                    }
                }
            }
            if (!isSeparator) {
                throw new ParserException("missing or wrong separator");
            }
        }
        if (rulesList.isEmpty()) {
            throw new ParserException("missing rules");
        }

    }


    private List<String> parseFactsLine(String factsLine) throws ParserException {

        if (factsLine == null || factsLine.length() == 0) {
            throw new ParserException("missing facts or empty line");
        }

        String[] knownFactLine = factsLine.trim().split(",");

        for (int k = 0; k < knownFactLine.length; k++) {
            knownFactLine[k] = knownFactLine[k].trim();
        }
        return Arrays.asList(knownFactLine);
    }


    private List parseRulesLine(String s) throws ParserException {

        List<String> ruleLineList = new ArrayList<>();

        if (s.length() == 0) {
            throw new ParserException("missing rules");
        }
        if (!s.contains("->")) {
            throw new ParserException("missing ->");
        }
        String[] split = s.split("((?<=&{2})|(?=&{2})|(?<=\\|{2})|(?=\\|{2})|(?<=->)|(?=->))");
        for (int i = 0; i < split.length; i++) {
            ruleLineList.add(split[i].trim());
        }
        return ruleLineList;
    }


    List<String> validateLine(List<String> line) throws ParserException {

        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).equals("||") || line.get(i).equals("&&") || line.get(i).equals("->")) {
            } else {

                boolean check = false;

                if (!Character.isLetter(line.get(i).charAt(0)) && !(line.get(i).charAt(0) == '_')
                        || line.get(i).length() == 1 && !Character.isLetter(line.get(i).charAt(0))) {
                    check = true;
                }

                for (int j = 1; j < line.get(i).length(); j++) {
                    if (!Character.isLetterOrDigit(line.get(i).charAt(j)) && line.get(i).charAt(j) != '_') {
                        check = true;
                    }
                }

                boolean b = false;
                for (int j = 0; j < line.get(i).length(); j++) {
                    if (Character.isLetter(line.get(i).charAt(j))) {
                        b = true;
                    }
                }
                if (!b || check) throw new ParserException("Wrong value " + line.get(i));

            }
        }
        return line;
    }
}