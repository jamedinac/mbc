package Interfaces;

import Common.ClusterResult;
import Common.GeneClusteringData;
import Common.GeneExpressionData;

public interface IClusterEvaluation {

    /**
     * Evaluates the clustering results
     * @param geneClusteringData
     * @return Cluster result
     */
    ClusterResult evaluate(GeneClusteringData geneClusteringData,  GeneExpressionData geneExpressionData);
}
