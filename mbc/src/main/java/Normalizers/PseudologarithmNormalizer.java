package Normalizers;

import Interfaces.IDataNormalizer;

public class PseudologarithmNormalizer implements IDataNormalizer {

    @Override
    public double[][] normalize(double[][] data, int[] replicatesPerTime, int[] sampleTimeMap, int numberOfTimeSeries) {
        int numberOfGenes = data.length;
        int numberOfSamples = data[0].length;
        double[][] logData = new double[numberOfGenes][numberOfSamples];

        for (int i = 0; i < numberOfGenes; i++) {
            for (int j = 0; j < numberOfSamples; j++) {
                logData[i][j] = Math.log(data[i][j] + 1);
            }
        }

        return logData;
    }
}
