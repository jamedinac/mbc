package MathAdapters;

import Interfaces.ILinearAlgebra;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

public class CommonsMathLinearAlgebra implements ILinearAlgebra {

    @Override
    public double[][] multiply(double[][] a, double[][] b) {
        RealMatrix matrixA = new Array2DRowRealMatrix(a);
        RealMatrix matrixB = new Array2DRowRealMatrix(b);
        return matrixA.multiply(matrixB).getData();
    }

    @Override
    public double[][] add(double[][] a, double[][] b) {
        RealMatrix matrixA = new Array2DRowRealMatrix(a);
        RealMatrix matrixB = new Array2DRowRealMatrix(b);
        return matrixA.add(matrixB).getData();
    }

    @Override
    public double[][] invert(double[][] matrix) {
        RealMatrix realMatrix = new Array2DRowRealMatrix(matrix);
        return new LUDecomposition(realMatrix).getSolver().getInverse().getData();
    }

    @Override
    public double[] getDiagonal(double[][] matrix) {
        int minDim = Math.min(matrix.length, matrix[0].length);
        double[] diag = new double[minDim];
        for (int i = 0; i < minDim; i++) {
            diag[i] = matrix[i][i];
        }
        return diag;
    }

    @Override
    public double[][] createScaledIdentity(int size, double scale) {
        double[][] identity = new double[size][size];
        for (int i = 0; i < size; i++) {
            identity[i][i] = scale;
        }
        return identity;
    }
}