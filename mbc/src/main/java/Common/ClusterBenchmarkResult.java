package Common;

public class ClusterBenchmarkResult {

    private BenchmarkType benchmarkType;
    double benchmarkValue;

    public ClusterBenchmarkResult(BenchmarkType benchmarkType, double benchmarkValue) {
        this.benchmarkType = benchmarkType;
        this.benchmarkValue = benchmarkValue;
    }
}
