package Interfaces;

import Common.GLMFitResult;

/**
 * Defines the contract for evaluating the statistical significance of estimated model parameters.
 * Implementations of this interface (such as the Wald Test) compute p-values to determine 
 * if the dynamic changes across time points are statistically significant.
 */
public interface ISignificanceTester {
    
    /**
     * Calculates raw, unadjusted P-values for the estimated coefficients.
     *
     * @param glmFit The result of the model fitting process containing betas, design matrix, and weights.
     * @return A 2D array of raw P-values [genes][timePoints].
     */
    double[][] calculateRawPValues(GLMFitResult glmFit);
}
