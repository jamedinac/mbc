package Interfaces;

import Common.GeneClusteringResult;
import Common.GeneExpressionData;

public interface IClusteringAlgorithm {

    /**
     * Groups the genes into clusters
     * @param geneExpresionData
     * @return
     */
    GeneClusteringResult clusterGenes(GeneExpressionData geneExpresionData);
}
