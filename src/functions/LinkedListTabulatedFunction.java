package functions;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class LinkedListTabulatedFunction implements TabulatedFunction, Externalizable, Cloneable {

    private static final long serialVersionUID = 1L;

    private static class FunctionNode {
        FunctionPoint point;
        FunctionNode prev;
        FunctionNode next;

        FunctionNode(FunctionPoint point) {
            this.point = point;
        }
    }

    private FunctionNode head;
    private int pointsCount;

    private void initEmptyList() {
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        pointsCount = 0;
    }

    public LinkedListTabulatedFunction() {
        initEmptyList();
    }


    public LinkedListTabulatedFunction(double leftX,
                                       double rightX,
                                       int pointsCount) {
        if (leftX >= rightX || pointsCount < 2) {
            throw new IllegalArgumentException("Некорректные границы или количество точек");
        }

        initEmptyList();

        double step = (rightX - leftX) / (pointsCount - 1);
        double x = leftX;

        for (int i = 0; i < pointsCount; i++) {
            addNodeToTail().point = new FunctionPoint(x, 0.0);
            x += step;
        }
    }

    public LinkedListTabulatedFunction(double leftX,
                                       double rightX,
                                       double[] values) {
        if (leftX >= rightX || values.length < 2) {
            throw new IllegalArgumentException("Некорректные границы или количество точек");
        }

        initEmptyList();

        double step = (rightX - leftX) / (values.length - 1);
        double x = leftX;

        for (int i = 0; i < values.length; i++) {
            addNodeToTail().point = new FunctionPoint(x, values[i]);
            x += step;
        }
    }

    public LinkedListTabulatedFunction(FunctionPoint[] sourcePoints) {
        if (sourcePoints.length < 2) {
            throw new IllegalArgumentException("Должно быть не меньше двух точек");
        }

        for (int i = 1; i < sourcePoints.length; i++) {
            if (sourcePoints[i - 1].getX() >= sourcePoints[i].getX()) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по возрастанию x");
            }
        }

        initEmptyList();

        for (int i = 0; i < sourcePoints.length; i++) {
            FunctionNode node = addNodeToTail();
            node.point = new FunctionPoint(sourcePoints[i]);
        }
    }

    public int getPointsCount() {
        return pointsCount;
    }


    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс точки вне диапазона: " + index);
        }

        FunctionNode current;
        if (index < pointsCount / 2) {
            current = head.next;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = head.prev;
            for (int i = pointsCount - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null);

        newNode.prev = head.prev;
        newNode.next = head;
        head.prev.next = newNode;
        head.prev = newNode;

        pointsCount++;
        return newNode;
    }

    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс точки вне диапазона для вставки: " + index);
        }

        if (index == pointsCount) {
            return addNodeToTail();
        }

        FunctionNode nextNode = getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode(null);

        newNode.next = nextNode;
        newNode.prev = nextNode.prev;
        nextNode.prev.next = newNode;
        nextNode.prev = newNode;

        pointsCount++;
        return newNode;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс точки вне диапазона для удаления: " + index);
        }

        FunctionNode node = getNodeByIndex(index);

        node.prev.next = node.next;
        node.next.prev = node.prev;

        pointsCount--;
        return node;
    }


    public double getLeftDomainBorder() {
        return head.next.point.getX();
    }

    public double getRightDomainBorder() {
        return head.prev.point.getX();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        FunctionNode current = head.next;
        while (current != head) {
            if (current.point.getX() == x) {
                return current.point.getY();
            }
            current = current.next;
        }

        current = head.next;
        while (current.next != head && !(current.point.getX() < x && x < current.next.point.getX())) {
            current = current.next;
        }

        double x1 = current.point.getX();
        double y1 = current.point.getY();
        double x2 = current.next.point.getX();
        double y2 = current.next.point.getY();

        return y1 + (x - x1) * (y2 - y1) / (x2 - x1);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс точки вне диапазона: " + index);
        }
    }

    public FunctionPoint getPoint(int index) {
        checkIndex(index);
        return new FunctionPoint(getNodeByIndex(index).point);
    }

    public void setPoint(int index,
                         FunctionPoint point) throws InappropriateFunctionPointException {
        checkIndex(index);
        double newX = point.getX();

        if (index > 0) {
            double leftX = getNodeByIndex(index - 1).point.getX();
            if (newX <= leftX) {
                throw new InappropriateFunctionPointException("x новой точки выходит за левый интервал");
            }
        }

        if (index < pointsCount - 1) {
            double rightX = getNodeByIndex(index + 1).point.getX();
            if (newX >= rightX) {
                throw new InappropriateFunctionPointException("x новой точки выходит за правый интервал");
            }
        }

        getNodeByIndex(index).point = new FunctionPoint(point);
    }

    public double getPointX(int index) {
        checkIndex(index);
        return getNodeByIndex(index).point.getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        checkIndex(index);

        if (index > 0) {
            double leftX = getNodeByIndex(index - 1).point.getX();
            if (x <= leftX) {
                throw new InappropriateFunctionPointException("x новой точки выходит за левый интервал");
            }
        }

        if (index < pointsCount - 1) {
            double rightX = getNodeByIndex(index + 1).point.getX();
            if (x >= rightX) {
                throw new InappropriateFunctionPointException("x новой точки выходит за правый интервал");
            }
        }

        getNodeByIndex(index).point.setX(x);
    }

    public double getPointY(int index) {
        checkIndex(index);
        return getNodeByIndex(index).point.getY();
    }

    public void setPointY(int index, double y) {
        checkIndex(index);
        getNodeByIndex(index).point.setY(y);
    }

    public void deletePoint(int index) {
        if (pointsCount < 3) {
            throw new IllegalStateException("Нельзя удалять точку: точек меньше трёх");
        }
        deleteNodeByIndex(index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        double x = point.getX();

        FunctionNode current = head.next;
        while (current != head) {
            if (current.point.getX() == x) {
                throw new InappropriateFunctionPointException("Точка с таким x уже существует: " + x);
            }
            current = current.next;
        }

        int insertIndex = 0;
        current = head.next;
        while (current != head && current.point.getX() < x) {
            insertIndex++;
            current = current.next;
        }

        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.point = new FunctionPoint(point);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);
        for (int i = 0; i < pointsCount; i++) {
            out.writeDouble(getPointX(i));
            out.writeDouble(getPointY(i));
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        initEmptyList();
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            FunctionNode node = addNodeToTail();
            node.point = new FunctionPoint(x, y);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');

        FunctionNode current = head.next;
        while (current != head) {
            sb.append(current.point.toString());
            if (current.next != head) {
                sb.append(", ");
            }
            current = current.next;
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

        FunctionNode current = this.head.next;
        int i = 0;
        while (current != this.head) {
            FunctionPoint otherPoint = new FunctionPoint(other.getPointX(i), other.getPointY(i));

            if (!current.point.equals(otherPoint)) {
                return false;
            }

            current = current.next;
            i++;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = pointsCount;

        FunctionNode current = head.next;
        while (current != head) {
            h ^= current.point.hashCode();
            current = current.next;
        }

        return h;
    }

    @Override
    public Object clone() {
        try {
            LinkedListTabulatedFunction copy = (LinkedListTabulatedFunction) super.clone();
            copy.initEmptyList();

            FunctionNode current = this.head.next;
            while (current != this.head) {
                FunctionPoint clonedPoint = (FunctionPoint) current.point.clone();
                FunctionNode node = copy.addNodeToTail();
                node.point = clonedPoint;
                current = current.next;
            }

            return copy;
        } catch (CloneNotSupportedException e) {
            LinkedListTabulatedFunction copy = new LinkedListTabulatedFunction();
            FunctionNode current = this.head.next;
            while (current != this.head) {
                FunctionNode node = copy.addNodeToTail();
                node.point = new FunctionPoint(current.point);
                current = current.next;
            }
            return copy;
        }
    }


}
