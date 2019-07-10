import model.Model;
import parcer.Parser;

import java.util.Collection;
import java.util.Iterator;


public class Deduction {

    private String filePath;


    public Deduction(String filePath) {
        this.filePath = filePath;
    }


    void deduce() {
        Model model = new Parser().parse(filePath);
        Collection<String> resultsList = model.deduce();

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
