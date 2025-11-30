package Interfaces;

import Common.ClusterResult;
import Common.GeneClusteringResult;
import Common.GeneExpressionData;

public interface IClusterBenchmark {

    /**
     * Evaluates the clustering results
     * @param geneClusteringData
     * @return Cluster result
     */
    ClusterResult evaluate(GeneClusteringResult geneClusteringData, GeneExpressionData geneExpressionData);
}
