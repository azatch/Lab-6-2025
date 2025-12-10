package functions;

import java.io.*;

public class TabulatedFunctions {

    private TabulatedFunctions() {
    }

    public static TabulatedFunction tabulate(Function function,
                                             double leftX,
                                             double rightX,
                                             int pointsCount) {
        if (leftX < function.getLeftDomainBorder()
                || rightX > function.getRightDomainBorder()
                || leftX >= rightX
                || pointsCount < 2) {
            throw new IllegalArgumentException("Некорректные границы табулирования или количество точек");
        }

        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        double x = leftX;

        for (int i = 0; i < pointsCount; i++) {
            values[i] = function.getFunctionValue(x);
            x += step;
        }

        return new ArrayTabulatedFunction(leftX, rightX, values);
    }

    public static void outputTabulatedFunction(TabulatedFunction function,
                                               OutputStream out) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out);

        int n = function.getPointsCount();
        dataOut.writeInt(n);
        for (int i = 0; i < n; i++) {
            dataOut.writeDouble(function.getPointX(i));
            dataOut.writeDouble(function.getPointY(i));
        }

        dataOut.flush();
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(in);

        int n = dataIn.readInt();
        FunctionPoint[] points = new FunctionPoint[n];

        for (int i = 0; i < n; i++) {
            double x = dataIn.readDouble();
            double y = dataIn.readDouble();
            points[i] = new FunctionPoint(x, y);
        }

        return new ArrayTabulatedFunction(points);
    }

    public static void writeTabulatedFunction(TabulatedFunction function,
                                              Writer out) throws IOException {
        BufferedWriter writer = new BufferedWriter(out);

        int n = function.getPointsCount();
        writer.write(Integer.toString(n));
        writer.newLine();

        for (int i = 0; i < n; i++) {
            double x = function.getPointX(i);
            double y = function.getPointY(i);
            writer.write(x + " " + y);
            writer.newLine();
        }

        writer.flush();
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);

        // читаем количество точек
        tokenizer.nextToken();
        int n = (int) tokenizer.nval;

        FunctionPoint[] points = new FunctionPoint[n];

        for (int i = 0; i < n; i++) {
            tokenizer.nextToken();
            double x = tokenizer.nval;

            tokenizer.nextToken();
            double y = tokenizer.nval;

            points[i] = new FunctionPoint(x, y);
        }

        return new ArrayTabulatedFunction(points);
    }
}
