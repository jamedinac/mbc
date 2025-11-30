package ClusterBenchmark;

import Common.BenchmarkType;
import Common.ClusterBenchmark;
import Common.GeneClusteringResult;
import Interfaces.IClusterBenchmark;
import org.example.ClusterBenchmarkService;

public class Jaccard implements IClusterBenchmark {

    GeneClusteringResult geneClustering;
    GeneClusteringResult goldStandard;

    public Jaccard(GeneClusteringResult geneClustering, GeneClusteringResult goldStandard) {
        this.geneClustering = geneClustering;
        this.goldStandard = goldStandard;
    }
    public ClusterBenchmarkService evaluate() {
        return new ClusterBenchmark(BenchmarkType.Jaccard, 1);
    }
}
