package Interfaces;

import Common.GeneExpressionData;

/**
 * Interface for loading raw gene expression data from an external source.
 */
public interface IDataLoad {

    /**
     * Converts gene expression source data to a structured format with columns per sample per time series.
     * 
     * @return a structured object containing the time series data matrix per gene
     */
    GeneExpressionData getGeneExpressionFormattedData();
}
