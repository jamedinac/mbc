package BenchmarkResult;

import Common.GeneClusterData;
import Enum.BenchmarkType;

public class ClusterBenchmarkResult {

    private final BenchmarkType benchmarkType;
    protected double[] benchmarkGeneValue;
    protected double benchmarkValue;
    protected GeneClusterData geneClusterData;

    public ClusterBenchmarkResult(BenchmarkType benchmarkType, double[] benchmarkGeneValue, double benchmarkValue, GeneClusterData geneClusterData) {
        this.benchmarkType = benchmarkType;
        this.benchmarkGeneValue = benchmarkGeneValue;
        this.benchmarkValue = benchmarkValue;
        this.geneClusterData = geneClusterData;
    }

    public BenchmarkType getBenchmarkType() {
        return benchmarkType;
    }

    public double[] getBenchmarkGeneValue() {
        return benchmarkGeneValue;
    }

    public double getBenchmarkValue () {
        return benchmarkValue;
    }

    public GeneClusterData getGeneClusterData() {
        return geneClusterData;
    }
}
