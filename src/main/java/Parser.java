
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Parser {
    private List<List<String>> rulesList = new ArrayList<>();
    private List<String> resultList = new ArrayList<>();


    public Model parse(String path) throws Exception {

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

        return new Model(rulesList, resultList);
    }


    private List<String> parseFactsLine(String factsLine) throws ParserException {

        if (factsLine == null || factsLine.length() == 0) {
            throw new ParserException("missing facts");
        }

        String[] knowingFactsLine = factsLine.trim().split(",");

        for (int k = 0; k < knowingFactsLine.length; k++) {
            knowingFactsLine[k] = knowingFactsLine[k].trim();
        }
        return Arrays.asList(knowingFactsLine);
    }


    private List parseRulesLine(String ruleLine) throws ParserException {

        List<String> ruleLineList = new ArrayList<>();

        if (ruleLine.length() == 0) {
            throw new ParserException("missing rules");
        }
        if (!ruleLine.contains("->")) {
            throw new ParserException("missing ->");
        }
        String[] split = ruleLine.split("((?<=&{2})|(?=&{2})|(?<=\\|{2})|(?=\\|{2})|(?<=->)|(?=->))");
        for (int i = 0; i < split.length; i++) {
            ruleLineList.add(split[i].trim());
        }
        return ruleLineList;
    }


    List<String> validateLine(List<String> line) throws ParserException {

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