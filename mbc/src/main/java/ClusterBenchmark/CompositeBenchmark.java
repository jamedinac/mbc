package ClusterBenchmark;

import BenchmarkResult.ClusterBenchmarkResult;
import BenchmarkResult.CompositeBenchmarkResult;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import Interfaces.IClusterBenchmark;

import java.util.ArrayList;
import java.util.List;

public class CompositeBenchmark implements IClusterBenchmark {

    private final List<IClusterBenchmark> benchmarks;

    public CompositeBenchmark() {
        this.benchmarks = new ArrayList<>();
    }

    public void addBenchmark(IClusterBenchmark benchmark) {
        this.benchmarks.add(benchmark);
    }

    @Override
    public ClusterBenchmarkResult evaluate(GeneExpressionData geneExpressionData, GeneClusterData geneClusterData) {
        List<ClusterBenchmarkResult> results = new ArrayList<>();
        
        for (IClusterBenchmark benchmark : this.benchmarks) {
            results.add(benchmark.evaluate(geneExpressionData, geneClusterData));
        }

        return new CompositeBenchmarkResult(results, geneClusterData);
    }
}
