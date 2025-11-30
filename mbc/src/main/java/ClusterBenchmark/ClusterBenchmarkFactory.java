package ClusterBenchmark;

import Common.BenchmarkType;
import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import Interfaces.IClusterBenchmark;

public class ClusterBenchmarkFactory {

    public static IClusterBenchmark create (BenchmarkType benchmarkType, GeneClusteringResult geneClusteringResult, GeneExpressionData geneExpressionData, GeneClusteringResult goldStandard) {
        IClusterBenchmark clusterBenchmark = null;

        switch (benchmarkType) {
            case Jaccard -> clusterBenchmark = new Jaccard(geneClusteringResult, goldStandard);
            case MeanSquaredError -> throw new UnsupportedOperationException("Not supported yet.");
            default -> throw new UnsupportedOperationException("Select a valid benchmark");
        }

        return clusterBenchmark;
    }
}
