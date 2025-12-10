package functions.basic;

import functions.Function;

public class Log implements Function {

    private final double base;

    public Log(double base) {
        this.base = base;
    }

    @Override
    public double getLeftDomainBorder() {
        return 0.0;
    }

    @Override
    public double getRightDomainBorder() {
        return Double.MAX_VALUE;
    }

    @Override
    public double getFunctionValue(double x) {
        return Math.log(x) / Math.log(base);
    }
}
