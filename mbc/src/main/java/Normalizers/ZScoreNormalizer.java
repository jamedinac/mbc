package Normalizers;

import Interfaces.IDataNormalizer;

public class ZScoreNormalizer implements IDataNormalizer {

    @Override
    public double[][] normalize(double[][] data, int[] replicatesPerTime, int[] sampleTimeMap, int numberOfTimeSeries) {
        int numberOfGenes = data.length;
        int numberOfSamples = data[0].length;
        double[][] zScoreData = new double[numberOfGenes][numberOfSamples];

        for (int i = 0; i < numberOfGenes; i++) {
            double mean = 0.0;
            double variance = 0.0;

            for (int j = 0; j < numberOfSamples; j++) {
                mean += data[i][j];
            }
            mean /= numberOfSamples;

            for (int j = 0; j < numberOfSamples; j++) {
                double difference = data[i][j] - mean;
                variance += difference * difference;
            }
            variance /= numberOfSamples - 1;

            double standardDeviation = Math.sqrt(variance);

            for (int j = 0; j < numberOfSamples; j++) {
                zScoreData[i][j] = (data[i][j] - mean) / standardDeviation;
            }
        }

        return zScoreData;
    }
}
