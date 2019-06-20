package parcer;


public enum Operators {

    AND {
        @Override
        String getSymbol() {
            return "&&";
        }
    },
    OR {
        @Override
        String getSymbol() {
            return "||";
        }
    },
    DEDUCTION {
        @Override
        String getSymbol() {
            return "->";
        }
    };

    abstract String getSymbol();

}
