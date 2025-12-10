package functions.meta;

import functions.Function;

public class Scale implements Function {

    private final Function f;
    private final double scaleX;
    private final double scaleY;

    public Scale(Function f, double scaleX, double scaleY) {
        this.f = f;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    @Override
    public double getLeftDomainBorder() {
        double left = f.getLeftDomainBorder();
        double right = f.getRightDomainBorder();

        if (scaleX > 0) {
            return left * scaleX;
        } else {
            return right * scaleX;
        }
    }

    @Override
    public double getRightDomainBorder() {
        double left = f.getLeftDomainBorder();
        double right = f.getRightDomainBorder();

        if (scaleX > 0) {
            return right * scaleX;
        } else {
            return left * scaleX;
        }
    }

    @Override
    public double getFunctionValue(double x) {
        double originalX = x / scaleX;
        double originalY = f.getFunctionValue(originalX);
        return originalY * scaleY;
    }
}
