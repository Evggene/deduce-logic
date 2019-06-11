import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Parser {
    private List<String> resultsList = new ArrayList<>();
    private Deduction deduction;
    private String pathToFile;


    public Parser(String pathToFile) {
        this.pathToFile = pathToFile;
    }


    public int validate() throws Exception {
        int countRulesLine = 0;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(pathToFile), Charset.forName("UTF-8"))) {
            String separator = "----------------------------------------------------------------";
            String readLine = null;
            boolean isSeparator = false;

            while ((readLine = br.readLine()) != null) {
                if (!readLine.equals(separator)) {
                    validateLine(parseRulesLine(readLine));
                    countRulesLine++;
                } else {
                    resultsList.addAll(validateLine(parseFactsLine(br.readLine())));
                    isSeparator = true;
                    break;
                }
            }
            if (!isSeparator) {
                throw new Exception("missing or wrong separator");
            }
            if (countRulesLine == 0) {
                throw new Exception("missing rules");
            }
        }
        return countRulesLine;
    }


    List<String> parse(int countRulesLine, Deduction deduction) throws Exception {
        int change = 0;
        for (int i = 0; i < countRulesLine; i++) {
            try (BufferedReader br = Files.newBufferedReader(Paths.get(pathToFile), Charset.forName("UTF-8"))) {
                for (int l = 0; l < countRulesLine - i; l++) {
                    List<String> list = parseRulesLine(br.readLine());
                    if (!resultsList.contains(list.get(list.size() - 1))) {
                        deduction.deduceFacts(list, resultsList);
                    }
                }
            }
            if (change == resultsList.size()) {
                break;
            } else {
                change = resultsList.size();
            }
        }
        return resultsList;
    }


    private List<String> parseFactsLine(String factsLine) throws Exception {

        if (factsLine == null || factsLine.length() == 0) {
            throw new Exception("missing facts or empty line");
        }

        String[] knownFactLine = factsLine.trim().split(",");

        for (int k = 0; k < knownFactLine.length; k++) {
            knownFactLine[k] = knownFactLine[k].trim();
        }
        return Arrays.asList(knownFactLine);
    }


    private List parseRulesLine(String s) throws Exception {

        List<String> ruleLineList = new ArrayList<>();

        if (s.length() == 0) {
            throw new Exception("missing rules");
        }
        if (!s.contains("->")) {
            throw new Exception("missing ->");
        }
        String[] split = s.split("((?<=&{2})|(?=&{2})|(?<=\\|{2})|(?=\\|{2})|(?<=->)|(?=->))");
        for (int i = 0; i < split.length; i++) {
            ruleLineList.add(split[i].trim());
        }
        return ruleLineList;
    }


    List<String> validateLine(List<String> line) throws Exception {

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
                if (!b || check) throw new Exception("Wrong value " + line.get(i));

            }
        }
        return line;
    }
}