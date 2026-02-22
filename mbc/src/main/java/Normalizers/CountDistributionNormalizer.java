package Normalizers;

import Interfaces.IDataNormalizer;

public class CountDistributionNormalizer implements IDataNormalizer {
    private int numberOfTimeSeries;
    private int numberOfReplicates;

    private IDataNormalizer medianRatioNormalizer = new MedianRatiosNormalization();

    public CountDistributionNormalizer(int numberOfReplicates, int numberOfTimeSeries) {
        this.numberOfTimeSeries = numberOfTimeSeries;
        this.numberOfReplicates = numberOfReplicates;
    }

    @Override
    public double[][] normalize(double[][] data) {
        data =  medianRatioNormalizer.normalize(data);

        double[][] estimatedMean = this.getEstimatedMean(data);
        return this.getProbabilityVector(estimatedMean);
    }

    public double[][] getEstimatedMean(double[][] data) {
        double[][] estimatedMean = new double[data.length][numberOfTimeSeries];

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < numberOfTimeSeries; j++) {
                estimatedMean[i][j] = 0;
                for (int c = 0; c<numberOfReplicates; c++) {
                    estimatedMean[i][j] += data[i][j*numberOfReplicates + c];
                }
                estimatedMean[i][j] /= numberOfReplicates;
            }
        }

        return  estimatedMean;
    }

    private double[][] getProbabilityVector(double[][] estimatedMean) {
        double[][] probabilityVector = new double[estimatedMean.length][estimatedMean[0].length];

        for (int i = 0; i < estimatedMean.length; i++) {
            double sum = 0.0;

            for (int j = 0; j < estimatedMean[i].length; j++) {
                sum += estimatedMean[i][j];
            }

            for (int j = 0; j < estimatedMean[i].length; j++) {
                probabilityVector[i][j] = estimatedMean[i][j] / sum;
            }
        }

        return probabilityVector;
    }
}
