package Interfaces;

import BenchmarkResult.ClusterBenchmarkResult;
import Common.GeneClusterData;
import Common.GeneExpressionData;

/**
 * Interface for evaluating the quality of a clustering result.
 */
public interface IClusterBenchmark {

    /**
     * Evaluates the clustering results based on a specific metric.
     *
     * @param geneExpressionData the processed gene expression data used for clustering
     * @param geneClusterData the resulting cluster assignments
     * @return an object containing the calculated benchmark score
     */
    ClusterBenchmarkResult evaluate(GeneExpressionData geneExpressionData, GeneClusterData geneClusterData);
}
