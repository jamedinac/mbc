package Interfaces;

/**
 * Defines the contract for applying multiple testing correction to a set of raw p-values.
 * This is crucial in high-throughput genomics to control the False Discovery Rate (FDR)
 * or Family-Wise Error Rate (FWER) when performing thousands of simultaneous hypothesis tests.
 */
public interface IMultipleTestingCorrection {
    
    /**
     * Adjusts the provided raw P-values to account for multiple hypothesis testing.
     *
     * @param rawPValues A 2D array of raw P-values [genes][timePoints] obtained from a significance test.
     * @return A 2D array of adjusted P-values (e.g., q-values or FDR) of the same dimensions.
     */
    double[][] adjustPValues(double[][] rawPValues);
}
