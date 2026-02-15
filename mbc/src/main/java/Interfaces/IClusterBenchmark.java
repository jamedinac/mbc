package Interfaces;

import Common.ClusterBenchmarkResult;
import Common.GeneClusterData;
import Common.GeneExpressionData;

public interface IClusterBenchmark {

    /**
     * Evaluates the clustering results
     *
     * @return Cluster result
     */
    ClusterBenchmarkResult evaluate(GeneExpressionData geneExpressionData, GeneClusterData geneClusterData);
}
