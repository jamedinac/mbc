package ClusterBenchmark;

import Common.BenchmarkType;
import Common.ClusterBenchmarkResult;
import Common.GeneClusteringResult;
import Interfaces.IClusterBenchmark;

public class Jaccard implements IClusterBenchmark {

    GeneClusteringResult geneClustering;
    GeneClusteringResult goldStandard;

    public Jaccard(GeneClusteringResult geneClustering, GeneClusteringResult goldStandard) {
        this.geneClustering = geneClustering;
        this.goldStandard = goldStandard;
    }
    public ClusterBenchmarkResult evaluate() {
        return new ClusterBenchmarkResult(BenchmarkType.Jaccard, 1);
    }
}
