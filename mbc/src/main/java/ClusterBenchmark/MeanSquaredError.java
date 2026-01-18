package ClusterBenchmark;

import Common.BenchmarkType;
import Common.ClusterBenchmarkResult;
import Interfaces.IClusterBenchmark;

public class MeanSquaredError implements IClusterBenchmark {

    public ClusterBenchmarkResult evaluate() {
        return new ClusterBenchmarkResult(BenchmarkType.Silhouette, 0.0);
    }
}
