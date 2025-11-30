package ClusterBenchmark;

import Common.BenchmarkType;
import Common.ClusterBenchmark;
import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import Interfaces.IClusterBenchmark;

public class JackardBenchmark implements IClusterBenchmark {

    GeneClusteringResult geneClustering;
    GeneClusteringResult goldStandard;

    public JackardBenchmark(GeneClusteringResult geneClustering, GeneClusteringResult goldStandard) {
        this.geneClustering = geneClustering;
        this.goldStandard = goldStandard;
    }
    public ClusterBenchmark evaluate() {
        return new ClusterBenchmark(BenchmarkType.Jackard, 1);
    }
}
