package Interfaces;

/**
 * Interface for compressing biological replicates into a single representative value per time point.
 */
public interface IReplicateCompression {

    /**
     * Compresses the replicates into a single value.
     * 
     * @param data the gene expression data matrix containing replicates
     * @param numberOfReplicates the number of biological replicates per time point
     * @param numberOfTimeSeries the total number of distinct time points
     * @return a new matrix where replicates are compressed to a single value per time point
     */
    double[][] compress(double[][] data, int numberOfReplicates, int numberOfTimeSeries);
}
