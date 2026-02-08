package Normalizers;

import Interfaces.IDataNormalizer;

public class PseudologarithmNormalizer implements IDataNormalizer {
    @Override
    public double[][] normalize(double[][] data) {
        double[][] normalizedData = new double[data.length][data[0].length];

        for (int r=0; r<data.length; r++) {
            for (int c=0; c<data[r].length; c++) {
                normalizedData[r][c] = Math.log(data[r][c] + 1);
            }
        }

        return normalizedData;
    }
}
