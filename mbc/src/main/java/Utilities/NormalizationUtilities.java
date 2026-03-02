package Utilities;

import java.util.Arrays;

public class NormalizationUtilities {
    public static double getMedian(double[] sampleData) {
        int n = sampleData.length;
        Arrays.sort(sampleData);
        return n%2 == 0 ? (sampleData[(n/2 - 1)] + sampleData[n/2]) / 2.0 : sampleData[n/2];
    }

    public static double getGeometricMean (double[] data) {
        int n =  data.length;
        double geometricMeanLog = 0.0;

        for (double d : data) geometricMeanLog += Math.log(d);

        return Math.exp(geometricMeanLog / n);
    }

    public static double[] getPseudoData(double[] data) {
        int n = data.length;
        double[] pseudoCount = new double[n];

        for (int i = 0; i < n; i++) pseudoCount[i] = data[i] + 1;

        return pseudoCount;
    }

    public static double[] getColumnMedian(double[][] data) {
        int n =  data.length;
        int m = data[0].length;

        double[] median = new  double[m];
        for (int i = 0; i < m; i++) {
            double[] column  = new double[n];
            for (int j=0; j<n; j++) column[j] = data[j][i];
            median[i] = NormalizationUtilities.getMedian(column);
        }
        return median;
    }

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
