package Interfaces;

import Common.GLMFitResult;

/**
 * Defines the contract for fitting a statistical model to normalized gene expression data.
 * This interface abstracts the model fitting process (e.g., Generalized Linear Models) 
 * from the data preprocessing pipeline.
 */
public interface IModelFitter {
    
    /**
     * Fits a statistical model to the provided expression data and returns the estimated parameters.
     *
     * @param normalizedData     A 2D array of normalized gene expression data [genes][samples].
     * @param replicatesPerTime  An array indicating the number of replicates available per time point.
     * @param sampleTimeMap      An array mapping each sample column index to its corresponding time point index.
     * @param numberOfTimeSeries The total number of distinct time points in the dataset.
     * @return A {@link GLMFitResult} record containing betas, weights, design matrix, and prior variance.
     */
    GLMFitResult fitModel(double[][] normalizedData, int[] replicatesPerTime, int[] sampleTimeMap, int numberOfTimeSeries);
}
