package Normalizers;

import Interfaces.IDataNormalizer;
import Interfaces.IReplicateCompression;
import ReplicateCompression.MeanReplicateCompression;

public class CountDistributionNormalizer implements IDataNormalizer {
    private final IReplicateCompression replicateCompression = new MeanReplicateCompression();

    public CountDistributionNormalizer() {
    }

    @Override
    public double[][] normalize(double[][] data, int[] replicatesPerTime, int[] sampleTimeMap, int numberOfTimeSeries) {
        // If data is already compressed (e.g., passed from DataProcessor after a compression step), 
        // replicatesPerTime should ideally reflect that (all 1s).
        double[][] estimatedMean = replicateCompression.compress(data, replicatesPerTime, numberOfTimeSeries);
        return this.getProbabilityVector(estimatedMean);
    }

    private double[][] getProbabilityVector(double[][] data) {
        double[][] probabilityVector = new double[data.length][data[0].length];

        for (int i = 0; i < data.length; i++) {
            double sum = 0.0;

            for (int j = 0; j < data[i].length; j++) {
                sum += data[i][j];
            }

            if (sum > 0) {
                for (int j = 0; j < data[i].length; j++) {
                    probabilityVector[i][j] = data[i][j] / sum;
                }
            }
        }

        return probabilityVector;
    }
}
