package Interfaces;

public interface IReplicateCompression {
    /**
     * Compress the replicates into a single value
     * @param data expression data
     * @param numberOfReplicates the number of replicates per time series
     * @param numberOfTimeSeries the number of time series
     * @return expression data with the replicates compress to a single value per time
     */
    double[][] compress(double[][] data, int numberOfReplicates, int numberOfTimeSeries);
}
