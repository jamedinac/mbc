package Interfaces;

/**
 * Interface representing a mathematical normalization strategy for gene expression matrices.
 */
public interface IDataNormalizer {

    /**
     * Normalizes the expression data to make samples comparable.
     * 
     * @param data the raw or partially processed expression matrix
     * @param replicatesPerTime the exact number of biological replicates per time point
     * @param sampleTimeMap an array mapping each column index to its corresponding time point index
     * @param numberOfTimeSeries the total number of distinct time points
     * @return the normalized expression matrix
     */
    double[][] normalize(double[][] data, int[] replicatesPerTime, int[] sampleTimeMap, int numberOfTimeSeries);
}
