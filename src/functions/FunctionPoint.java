package functions;

import java.io.Serializable;

public class FunctionPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    private double x;
    private double y;

    public FunctionPoint(double x, double y){
        this.x = x;
        this.y = y;
    }

    public FunctionPoint(FunctionPoint point){
        this.x = point.getX();
        this.y = point.getY();
    }

    public FunctionPoint(){
        this(0.0, 0.0);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctionPoint)) return false;
        FunctionPoint that = (FunctionPoint) o;

        double eps = 1e-9;
        return Math.abs(this.x - that.x) < eps &&
                Math.abs(this.y - that.y) < eps;
    }

    @Override
    public int hashCode() {
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);

        int xLow  = (int) (xBits & 0xFFFFFFFFL);
        int xHigh = (int) (xBits >>> 32);
        int yLow  = (int) (yBits & 0xFFFFFFFFL);
        int yHigh = (int) (yBits >>> 32);

        return xLow ^ xHigh ^ yLow ^ yHigh;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return new FunctionPoint(this.x, this.y);
        }
    }
}
