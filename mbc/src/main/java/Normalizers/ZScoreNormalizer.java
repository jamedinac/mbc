package Normalizers;

import Interfaces.IDataNormalizer;

public class ZScoreNormalizer implements IDataNormalizer {
    @Override
    public double[][] normalize(double[][] data) {
        double[][] normalizedData = new double[data.length][data[0].length];

        for (int r=0; r<data.length; r++) {
            double mean = 0;
            double standardDeviation = 0;

            for (int c=0; c<data[r].length; c++) {
                mean += data[r][c];
            }
            mean /= data[r].length;

            for (int c=0; c<data[r].length; c++) {
                standardDeviation += (mean - data[r][c]) * (mean - data[r][c]);
            }
            standardDeviation = Math.sqrt(standardDeviation / data[r].length);

            for (int c=0; c<data[r].length; c++) {
                normalizedData[r][c] = (data[r][c] - mean) / standardDeviation;
            }
        }

        return normalizedData;
    }
}
