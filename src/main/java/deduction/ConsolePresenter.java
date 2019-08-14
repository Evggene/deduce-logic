package deduction;

import java.util.Collection;
import java.util.Iterator;

public class ConsolePresenter implements Presenter {

    @Override
    public void showError(String message) {
        System.err.print(message);
    }

    @Override
    public void showError(Throwable e) {
        System.err.print(e.getMessage());

    }

    @Override
    public void showInfo(String message) {
        System.err.print(message);
    }

    @Override
    public void showResult(Collection<String> resultsList) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> i = resultsList.iterator();
        if (i.hasNext())
            sb.append(i.next());
        while (i.hasNext()) {
            sb.append(", ").append(i.next());
        }
        System.out.print(sb);
    }
}
