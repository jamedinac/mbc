package Interfaces;

import Common.GeneExpressionData;

public interface IGeneExpressionDataLoad {

    /**
     * Converts gene expression source data to a matrix with columns per sample per time series
     * @return Matrix with the time series data per gene
     */
    GeneExpressionData getGeneExpressionFormattedData();

    /**
     * Adds a filter to filter out genes
     * @param filter the filter to apply
     */
    void addGeneFilter(IGeneFilter filter);

    /**
     * Adds a sample valid trait
     * @param sampleTrait sample trait name
     * @param sampleTraitValue sample trait value
     */
    void addSampleFilter(String sampleTrait, String sampleTraitValue);

    /**
     * Adds a normalizer to apply to data
     * @param normalizer the normalizer to apply
     */
    void addNormalizer(IDataNormalizer normalizer);
}
