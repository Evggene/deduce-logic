public enum LogicOperations {

    AND {
        @Override
        String operationSymbol() {
            return "&&";
        }

        @Override
        boolean doOperation(boolean a, boolean b) {
            return a && b;
        }
    },
    OR {
        @Override
        String operationSymbol() {
            return "||";
        }

        @Override
        boolean doOperation(boolean a, boolean b) {
            return a || b;
        }
    },
    DEDUCTION {
        @Override
        String operationSymbol() {
            return "->";
        }

        @Override
        boolean doOperation(boolean a, boolean b) {
            return false;
        }
    };

    abstract String operationSymbol();

    abstract boolean doOperation(boolean a, boolean b);
}
