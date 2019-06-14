import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Reader {

    public Model readAndParse(String path, Parser parser) throws Exception {

        List<RuleLine> rulesList = new ArrayList<>();
        List<String> resultList = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(path), Charset.forName("UTF-8"))) {
            String separator = "----------------------------------------------------------------";
            String readLine = null;
            boolean isSeparator = false;

            while ((readLine = br.readLine()) != null) {
                if (!readLine.equals(separator)) {

                    rulesList.add(new RuleLine(parser.parseRulesLine(readLine)));
                } else {
                    resultList.addAll(parser.parseFactsLine(br.readLine()));
                    isSeparator = true;
                    break;
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
}
