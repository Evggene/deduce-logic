
import java.util.ArrayList;
import java.util.List;


public class Model {
    private List<List<String>> rulesList;
    private List<String> resultList;


    public Model(List<List<String>> rulesList, List<String> resultList) {
        this.rulesList = rulesList;
        this.resultList = resultList;
    }


    public void deduce() {
        int change = 0;
        for (int i = 0; i < rulesList.size(); i++) {
            for (int j = 0; j < rulesList.size() - i; j++) {
                ArrayList al = new ArrayList(rulesList.get(j));
                if (!resultList.contains(al.get(al.size() - 1))) {
                    deduce(al);
                }
            }
            if (change == resultList.size()) {
                break;
            } else {
                change = resultList.size();
            }
        }
    }


    private void deduce(List<String> valuesRules) {

        boolean currentOperation = false;

        if (valuesRules.size() == 3) {
            if (resultList.contains(valuesRules.get(0))) {
                if (!resultList.contains(valuesRules.get(2))) {
                    resultList.add(valuesRules.get(2));
                }
            }
        }
        for (int i = 0; i < valuesRules.size() - 1; i++) {
            if (valuesRules.get(i).equals("true")
                    || valuesRules.get(i).equals("false")
                    || valuesRules.get(i).equals("&&")
                    || valuesRules.get(i).equals("||")
                    || valuesRules.get(i).equals("->")) {
            } else {
                if (resultList.contains(valuesRules.get(i))) {
                    valuesRules.set(i, "true");
                } else {
                    valuesRules.set(i, "false");
                }
            }
        }
        for (int i = 0; i < valuesRules.size(); i++) {
            if (valuesRules.get(i).equals("->")) {
                break;
            }
            if (valuesRules.get(i).equals("&&")) {
                if (Boolean.valueOf(valuesRules.get(i - 1)) && Boolean.valueOf(valuesRules.get(i + 1))) {
                    currentOperation = true;
                } else {
                    currentOperation = false;
                }
                valuesRules.remove(i - 1);
                valuesRules.remove(i - 1);
                valuesRules.set(i - 1, String.valueOf(currentOperation));
            }
        }
        for (int i = 0; i < valuesRules.size(); i++) {
            if (valuesRules.get(i).equals("->")) {
                break;
            }
            if (valuesRules.get(i).equals("||")) {
                if (Boolean.valueOf(valuesRules.get(i - 1)) || Boolean.valueOf(valuesRules.get(i + 1))) {
                    currentOperation = true;
                } else {
                    currentOperation = false;
                }
                valuesRules.remove(i - 1);
                valuesRules.remove(i - 1);
                valuesRules.set(i - 1, String.valueOf(currentOperation));
            }
        }
        if (currentOperation == true) {
            if (!resultList.contains(valuesRules.get(2))) {
                resultList.add(valuesRules.get(2));
            }
        }
    }
}