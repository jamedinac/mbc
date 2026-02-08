package ClusterBenchmark;

import Common.BenchmarkType;
import Common.GeneClusteringResult;
import Interfaces.IClusterBenchmark;

public class ClusterBenchmarkFactory {

    public static IClusterBenchmark create (BenchmarkType benchmarkType, GeneClusteringResult geneClusteringResult, GeneClusteringResult goldStandard) {
        IClusterBenchmark clusterBenchmark = null;

        switch (benchmarkType) {
            case  Silhouette -> clusterBenchmark = new Silhouette(geneClusteringResult);
            case Jaccard -> clusterBenchmark = new Jaccard(geneClusteringResult, goldStandard);
            case MeanSquaredError -> throw new UnsupportedOperationException("Not supported yet.");
            default -> throw new UnsupportedOperationException("Select a valid benchmark");
        }

        return clusterBenchmark;
    }
}
