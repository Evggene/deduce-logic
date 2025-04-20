package deduction.presenter;

import java.util.Collection;

public interface Presenter {
    void showError(String message);
    void showError(Throwable e);
    void showInfo(String message);
    void showResult(Collection<String> strings);
}
