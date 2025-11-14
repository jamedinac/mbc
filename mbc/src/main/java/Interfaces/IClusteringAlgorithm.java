package Interfaces;

import Common.GeneClusteringData;
import Common.GeneExpressionData;

public interface IClusteringAlgorithm {

    /**
     * Groups the genes into clusters
     * @param geneExpresionData
     * @return
     */
    GeneClusteringData clusterGenes(GeneExpressionData geneExpresionData);
}
