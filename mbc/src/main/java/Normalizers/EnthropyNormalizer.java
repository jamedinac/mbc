package Normalizers;

import Interfaces.IDataNormalizer;

public class EnthropyNormalizer implements IDataNormalizer {

    private final double epsilon = 1e-6;
    @Override
    public double[][] normalize(double[][] data) {
        double[][] normalizedData = new double[data.length][data[0].length];

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                normalizedData[i][j] = calculateEntropy(data[i][j]);
            }
        }

        return normalizedData;
    }

    private double calculateEntropy(double data) {
        double entropy = Math.log(2.0) + Math.log(Math.PI) + Math.log(Math.E) + Math.log(data + epsilon);
        return entropy / 2.0;
    }
}
