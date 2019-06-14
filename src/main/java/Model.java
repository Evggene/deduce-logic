
import java.util.ArrayList;
import java.util.List;


public class Model {
    private List<RuleLine> rulesList;
    private List<String> knowingFactsList;


    public Model(List<RuleLine> rulesList, List<String> knowingFactsList) {
        this.rulesList = rulesList;
        this.knowingFactsList = knowingFactsList;
    }


    public List<String> deduce() {
        int change = 0;
        for (int i = 0; i < rulesList.size(); i++) {
            for (int j = 0; j < rulesList.size() - i; j++) {
                rulesList.get(j).deduceLine(knowingFactsList);
            }
            if (change == knowingFactsList.size()) {
                break;
            } else {
                change = knowingFactsList.size();
            }
        }
        return knowingFactsList;
    }
}