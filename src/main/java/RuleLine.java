import java.util.ArrayList;
import java.util.List;

public class RuleLine {

    private List<String> line;

    public RuleLine(List<String> ruleLine) {
        this.line = ruleLine;

    }

    public void deduceLine(List<String> resultList) {

        ArrayList<String> ruleLine = new ArrayList<>(line);
        List<String> expression = ruleLine.subList(0, ruleLine.indexOf("->"));
        String deducingFact = ruleLine.get(ruleLine.indexOf("->") + 1);

        boolean currentOperation = false;

        if (expression.size() == 1) {
            if (resultList.contains(expression.get(0))) {
                if (!resultList.contains(deducingFact)) {
                    resultList.add(deducingFact);
                }
            }
        }

        compateTrueFalse(expression, resultList);

        for (int j = 0; j < LogicOperations.values().length; j++) {

            for (int i = 0; i < expression.size(); i++) {
                if (expression.get(i).equals(LogicOperations.values()[j].operationSymbol())) {
                    if (LogicOperations.values()[j].doOperation(
                            Boolean.valueOf(expression.get(i - 1)), Boolean.valueOf(expression.get(i + 1)))) {
                        currentOperation = true;
                    } else {
                        currentOperation = false;
                    }
                    expression.remove(i - 1);
                    expression.remove(i - 1);
                    expression.set(i - 1, String.valueOf(currentOperation));
                }
            }
            if (currentOperation == true) {
                if (!resultList.contains(deducingFact)) {
                    resultList.add(deducingFact);
                }
            }
        }
    }

    private void compateTrueFalse(List<String> expression, List<String> resultList) {
        for (int i = 0; i < expression.size(); i++) {
            if (!expression.get(i).equals("true")
                    && !expression.get(i).equals("false")
                    && !expression.get(i).equals(LogicOperations.AND.operationSymbol())
                    && !expression.get(i).equals(LogicOperations.OR.operationSymbol())
                    && !expression.get(i).equals(LogicOperations.DEDUCTION.operationSymbol())) {
                if (resultList.contains(expression.get(i))) {
                    expression.set(i, "true");
                } else {
                    expression.set(i, "false");
                }
            }
        }
    }
}
