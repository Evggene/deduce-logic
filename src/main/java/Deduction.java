
import java.util.ArrayList;
import java.util.List;


public class Deduction {



    void deduceFacts(List<String> valuesRules, List<String> resultList) {

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