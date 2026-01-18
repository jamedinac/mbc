package Interfaces;

import Common.ClusterBenchmarkResult;

public interface IClusterBenchmark {

    /**
     * Evaluates the clustering results
     *
     * @return Cluster result
     */
    ClusterBenchmarkResult evaluate();
}
