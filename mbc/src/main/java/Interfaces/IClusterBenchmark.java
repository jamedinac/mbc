package Interfaces;

import org.example.ClusterBenchmarkService;

public interface IClusterBenchmark {

    /**
     * Evaluates the clustering results
     *
     * @return Cluster result
     */
    ClusterBenchmarkService evaluate();
}
