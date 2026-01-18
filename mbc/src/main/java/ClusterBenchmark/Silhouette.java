package ClusterBenchmark;

import Common.BenchmarkType;
import Common.ClusterBenchmarkResult;
import Common.GeneClusteringResult;
import Interfaces.IClusterBenchmark;

public class Silhouette implements IClusterBenchmark {

    GeneClusteringResult geneClustering;
    GeneClusteringResult goldStandard;

    @Override
    public ClusterBenchmarkResult evaluate() {
        return new ClusterBenchmarkResult(BenchmarkType.Silhouette, 0.0);
    }
}
