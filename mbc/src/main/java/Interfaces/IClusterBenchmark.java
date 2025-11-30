package Interfaces;

import Common.ClusterBenchmark;

public interface IClusterBenchmark {

    /**
     * Evaluates the clustering results
     * @return Cluster result
     */
    ClusterBenchmark evaluate();
}
