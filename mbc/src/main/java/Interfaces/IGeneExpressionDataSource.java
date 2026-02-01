package Interfaces;

import Common.GeneExpressionData;

public interface IGeneExpressionDataSource {

    /**
     * Converts gene expression source data to a matrix with columns per sample per time series
     * @return Matrix with the time series data per gene
     */
    GeneExpressionData getGeneExpressionFormattedData(int numberOfTimeSeries, int numberOfReplicates);
}
