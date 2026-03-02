package Normalizers;

import Interfaces.IDataNormalizer;
import Interfaces.IReplicateCompression;
import ReplicateCompression.MeanReplicateCompression;

public class CountDistributionNormalizer implements IDataNormalizer {
    private final int numberOfTimeSeries;
    private final int numberOfReplicates;

    IReplicateCompression replicateCompression = new MeanReplicateCompression();

    public CountDistributionNormalizer(int numberOfReplicates, int numberOfTimeSeries) {
        this.numberOfTimeSeries = numberOfTimeSeries;
        this.numberOfReplicates = numberOfReplicates;
    }

    @Override
    public double[][] normalize(double[][] data) {
        double[][] estimatedMean = replicateCompression.compress(data, numberOfReplicates, numberOfTimeSeries);
        return this.getProbabilityVector(estimatedMean);
    }

    private double[][] getProbabilityVector(double[][] data) {
        double[][] probabilityVector = new double[data.length][data[0].length];

        for (int i = 0; i < data.length; i++) {
            double sum = 0.0;

            for (int j = 0; j < data[i].length; j++) {
                sum += data[i][j];
            }

            for (int j = 0; j < data[i].length; j++) {
                probabilityVector[i][j] = data[i][j] / sum;
            }
        }

        return probabilityVector;
    }
}
