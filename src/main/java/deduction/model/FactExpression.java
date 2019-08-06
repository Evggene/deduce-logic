package deduction.model;

import deduction.Wrapper;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;


public class FactExpression implements Expression {

    private static Collection<Expression> arrayList = new ArrayList<>();

    @XmlAttribute()
    private String fact;

    public FactExpression() {
    }

    public FactExpression(String fact) {
        this.fact = fact;
    }

    @Override
    public boolean calculate(Collection<String> knownFacts) {
        return knownFacts.contains(fact);
    }


    public Collection<Expression> getExpressions() {
        return arrayList;
    }

    @Override
    public Object accept(Wrapper t) {
        return t.wrap(this);
    }

    @Override
    public String toString() {
        return fact;
    }
}
