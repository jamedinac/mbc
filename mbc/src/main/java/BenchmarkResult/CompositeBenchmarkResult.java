package BenchmarkResult;

import Common.GeneClusterData;
import Enum.BenchmarkType;
import java.util.List;

public class CompositeBenchmarkResult extends ClusterBenchmarkResult {

    private final List<ClusterBenchmarkResult> results;

    public CompositeBenchmarkResult(List<ClusterBenchmarkResult> results, GeneClusterData geneClusterData) {
        // Pass dummy values to the superclass; we're mostly using this as an aggregate container
        super(BenchmarkType.Silhouette, new double[0], 0.0, geneClusterData);
        this.results = results;
    }

    public List<ClusterBenchmarkResult> getResults() {
        return results;
    }
}
