package functions;

import functions.meta.*;

public class Functions {

    private Functions() {
    }

    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }

    public static Function scale(Function f, double scaleX, double scaleY) {
        return new Scale(f, scaleX, scaleY);
    }

    public static Function power(Function f, double power) {
        return new Power(f, power);
    }

    public static Function sum(Function f1, Function f2) {
        return new Sum(f1, f2);
    }

    public static Function mult(Function f1, Function f2) {
        return new Mult(f1, f2);
    }

    public static Function composition(Function f1, Function f2) {
        return new Composition(f1, f2);
    }

    public static double integrate(Function f, double leftX, double rightX, double step)
            throws FunctionPointIndexOutOfBoundsException {

        if (leftX < f.getLeftDomainBorder() || rightX > f.getRightDomainBorder()) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Интервал интегрирования выходит за границы области определения функции"
            );
        }

        double integral = 0.0;
        double x1 = leftX;

        while (x1 < rightX) {
            double x2 = Math.min(x1 + step, rightX);
            double y1 = f.getFunctionValue(x1);
            double y2 = f.getFunctionValue(x2);
            double trapezoidArea = (y1 + y2) / 2.0 * (x2 - x1);
            integral += trapezoidArea;
            x1 = x2;
        }

        return integral;
    }
}
