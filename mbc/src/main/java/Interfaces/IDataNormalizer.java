package Interfaces;

/**
 * Interface representing a mathematical normalization strategy for gene expression matrices.
 */
public interface IDataNormalizer {

    /**
     * Normalizes the expression data to make samples comparable.
     * 
     * @param data the raw or partially processed expression matrix
     * @return the normalized expression matrix
     */
    double[][] normalize(double[][] data);
}
