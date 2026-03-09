package Interfaces;

import Common.GeneClusterData;
import Common.GeneExpressionData;

/**
 * Interface defining a clustering algorithm that groups gene expression data.
 */
public interface IClusteringAlgorithm {

    /**
     * Groups the genes into clusters based on their expression profiles.
     * 
     * @param geneExpressionData the processed gene expression matrix
     * @return the resulting cluster assignments for the given data
     */
    GeneClusterData clusterGenes(GeneExpressionData geneExpressionData);
}
