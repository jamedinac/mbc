package Utilities;

import java.util.Arrays;

/**
 * Utility class providing mathematical functions for data normalization processes.
 */
public class NormalizationUtilities {
    
    /**
     * Calculates the median value of a double array.
     * 
     * @param sampleData the array of numerical values
     * @return the calculated median
     */
    public static double getMedian(double[] sampleData) {
        int n = sampleData.length;
        Arrays.sort(sampleData);
        return n % 2 == 0 ? (sampleData[(n / 2 - 1)] + sampleData[n / 2]) / 2.0 : sampleData[n / 2];
    }

    /**
     * Calculates the geometric mean of a double array.
     * 
     * @param data the array of numerical values
     * @return the geometric mean
     */
    public static double getGeometricMean(double[] data) {
        int n =  data.length;
        double geometricMeanLog = 0.0;

        for (double d : data) geometricMeanLog += Math.log(d);

        return Math.exp(geometricMeanLog / n);
    }

    /**
     * Adds a pseudo-count of 1 to each element in a double array to avoid zeroes.
     * 
     * @param data the original data array
     * @return a new array with pseudo-counts added
     */
    public static double[] getPseudoData(double[] data) {
        int n = data.length;
        double[] pseudoCount = new double[n];

        for (int i = 0; i < n; i++) pseudoCount[i] = data[i] + 1;

        return pseudoCount;
    }

    /**
     * Calculates the median for each column in a 2D matrix.
     * 
     * @param data the 2D data matrix [rows][columns]
     * @return an array containing the median value of each column
     */
    public static double[] getColumnMedian(double[][] data) {
        int n =  data.length;
        int m = data[0].length;

        double[] median = new  double[m];
        for (int i = 0; i < m; i++) {
            double[] column  = new double[n];
            for (int j = 0; j < n; j++) column[j] = data[j][i];
            median[i] = NormalizationUtilities.getMedian(column);
        }
        return median;
    }

    /**
     * Divides each element in a matrix by its corresponding row divisor.
     * 
     * @param data the 2D data matrix
     * @param row an array of divisors corresponding to each row
     * @return a new matrix containing the divided values
     */
    public static double[][] getDivideByRow(double[][] data, double[] row) {
        int n = data.length;
        int m = data[0].length;

        double[][] divideByRow = new double[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                divideByRow[i][j] = data[i][j] / row[i];
            }
        }

        return divideByRow;
    }

    /**
     * Divides each element in a matrix by its corresponding column divisor.
     * 
     * @param data the 2D data matrix
     * @param column an array of divisors corresponding to each column
     * @return a new matrix containing the divided values
     */
    public static double[][] getDivideByColumn(double[][] data, double[] column) {
        int n = data.length;
        int m = data[0].length;

        double[][] divideByColumn = new double[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                divideByColumn[i][j] = data[i][j] / column[j];
            }
        }

        return divideByColumn;
    }
}
