public class Main {

    public static void main(String[] args) {

        if (args == null || args.length == 0) {
            System.out.print("Missing argument");
            return;
        }

        new Deduction(args[0]).deduce();

    }
}