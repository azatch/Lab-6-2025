package functions.meta;

import functions.Function;

public class Composition implements Function {

    private final Function outer;
    private final Function inner;

    public Composition(Function outer, Function inner) {
        this.outer = outer;
        this.inner = inner;
    }

    @Override
    public double getLeftDomainBorder() {
        return inner.getLeftDomainBorder();
    }

    @Override
    public double getRightDomainBorder() {
        return inner.getRightDomainBorder();
    }

    @Override
    public double getFunctionValue(double x) {
        double innerValue = inner.getFunctionValue(x);
        return outer.getFunctionValue(innerValue);
    }
}
