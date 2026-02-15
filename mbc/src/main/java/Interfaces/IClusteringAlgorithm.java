package Interfaces;

import Common.GeneClusterData;
import Common.GeneExpressionData;

public interface IClusteringAlgorithm {

    /**
     * Groups the genes into clusters
     * @param geneExpresionData
     * @return
     */
    GeneClusterData clusterGenes(GeneExpressionData geneExpresionData);
}
