package Interfaces;

import Common.GeneExpressionData;

/**
 * Interface for the entire data processing pipeline, including filtering, normalization, and compression.
 */
public interface IDataProcessor {

    /**
     * Processes raw gene expression data through a defined pipeline.
     * 
     * @param rawData the raw gene expression data object
     * @return the fully processed and normalized gene expression data
     */
    GeneExpressionData processData(GeneExpressionData rawData);
}
