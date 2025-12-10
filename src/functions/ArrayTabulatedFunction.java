package functions;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.Serializable;

public class ArrayTabulatedFunction implements TabulatedFunction, Serializable, Cloneable {

    private FunctionPoint[] points;
    private int pointsCount;

    public ArrayTabulatedFunction(double leftX,
                                  double rightX,
                                  int pointsCount) {
        if (leftX >= rightX || pointsCount < 2) {
            throw new IllegalArgumentException("Некорректные границы или количество точек");
        }

        this.pointsCount = pointsCount;
        this.points = new FunctionPoint[pointsCount];

        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + step * i;
            double y = 0.0;
            points[i] = new FunctionPoint(x, y);
        }
    }

    public ArrayTabulatedFunction(double leftX,
                                  double rightX,
                                  double[] values) {
        if (leftX >= rightX || values.length < 2) {
            throw new IllegalArgumentException("Некорректные границы или количество точек");
        }

        this.pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount];

        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + step * i;
            double y = values[i];
            points[i] = new FunctionPoint(x, y);
        }
    }

    public ArrayTabulatedFunction(FunctionPoint[] sourcePoints) {
        if (sourcePoints.length < 2) {
            throw new IllegalArgumentException("Должно быть не меньше двух точек");
        }

        for (int i = 1; i < sourcePoints.length; i++) {
            if (sourcePoints[i - 1].getX() >= sourcePoints[i].getX()) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по возрастанию x");
            }
        }

        this.pointsCount = sourcePoints.length;
        this.points = new FunctionPoint[pointsCount];

        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(sourcePoints[i]);
        }
    }

    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        for (int i = 0; i < pointsCount; i++) {
            if (points[i].getX() == x) {
                return points[i].getY();
            }
        }

        int i = 0;
        while (!(points[i].getX() < x && x < points[i + 1].getX())) {
            i++;
        }

        double x1 = points[i].getX();
        double y1 = points[i].getY();
        double x2 = points[i + 1].getX();
        double y2 = points[i + 1].getY();

        return y1 + (x - x1) * (y2 - y1) / (x2 - x1);
    }

    public int getPointsCount() {
        return pointsCount;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс точки вне диапазона: " + index);
        }
    }

    public FunctionPoint getPoint(int index) {
        checkIndex(index);
        return new FunctionPoint(points[index]);
    }

    public void setPoint(int index,
                         FunctionPoint point) throws InappropriateFunctionPointException {
        checkIndex(index);
        double newX = point.getX();

        if (index > 0) {
            double leftX = points[index - 1].getX();
            if (newX <= leftX) {
                throw new InappropriateFunctionPointException("x новой точки выходит за левый интервал");
            }
        }

        if (index < pointsCount - 1) {
            double rightX = points[index + 1].getX();
            if (newX >= rightX) {
                throw new InappropriateFunctionPointException("x новой точки выходит за правый интервал");
            }
        }

        points[index] = new FunctionPoint(point);
    }

    public double getPointX(int index) {
        checkIndex(index);
        return points[index].getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        checkIndex(index);

        if (index > 0) {
            double leftX = points[index - 1].getX();
            if (x <= leftX) {
                throw new InappropriateFunctionPointException("x новой точки выходит за левый интервал");
            }
        }

        if (index < pointsCount - 1) {
            double rightX = points[index + 1].getX();
            if (x >= rightX) {
                throw new InappropriateFunctionPointException("x новой точки выходит за правый интервал");
            }
        }

        points[index].setX(x);
    }

    public double getPointY(int index) {
        checkIndex(index);
        return points[index].getY();
    }

    public void setPointY(int index, double y) {
        checkIndex(index);
        points[index].setY(y);
    }

    public void deletePoint(int index) {
        if (pointsCount < 3) {
            throw new IllegalStateException("Нельзя удалять точку: точек меньше трёх");
        }

        checkIndex(index);

        if (index < pointsCount - 1) {
            System.arraycopy(points,
                    index + 1,
                    points,
                    index,
                    pointsCount - index - 1);
        }

        pointsCount--;
        points[pointsCount] = null;
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        double x = point.getX();

        for (int i = 0; i < pointsCount; i++) {
            if (points[i].getX() == x) {
                throw new InappropriateFunctionPointException("Точка с таким x уже существует: " + x);
            }
        }

        if (pointsCount == points.length) {
            return;
        }

        int insertIndex = 0;
        while (insertIndex < pointsCount && points[insertIndex].getX() < x) {
            insertIndex++;
        }

        if (insertIndex < pointsCount) {
            System.arraycopy(points,
                    insertIndex,
                    points,
                    insertIndex + 1,
                    pointsCount - insertIndex);
        }

        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (int i = 0; i < pointsCount; i++) {
            sb.append(points[i].toString());
            if (i != pointsCount - 1) {
                sb.append(", ");
            }
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabulatedFunction)) return false;
        TabulatedFunction other = (TabulatedFunction) o;

        if (this.getPointsCount() != other.getPointsCount()) {
            return false;
        }

        for (int i = 0; i < pointsCount; i++) {
            FunctionPoint thisPoint = this.points[i];
            FunctionPoint otherPoint = new FunctionPoint(other.getPointX(i), other.getPointY(i));

            if (!thisPoint.equals(otherPoint)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int h = pointsCount;
        for (int i = 0; i < pointsCount; i++) {
            h ^= points[i].hashCode();
        }
        return h;
    }

    @Override
    public Object clone() {
        try {
            ArrayTabulatedFunction copy = (ArrayTabulatedFunction) super.clone();
            copy.points = new FunctionPoint[this.pointsCount];
            for (int i = 0; i < this.pointsCount; i++) {
                copy.points[i] = (FunctionPoint) this.points[i].clone();
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            FunctionPoint[] newPoints = new FunctionPoint[this.pointsCount];
            for (int i = 0; i < this.pointsCount; i++) {
                newPoints[i] = new FunctionPoint(this.points[i]);
            }
            return new ArrayTabulatedFunction(newPoints);
        }
    }


}
