package ClusterBenchmark;

import Enum.BenchmarkType;
import Common.GeneClusterData;
import Interfaces.IClusterBenchmark;
import Interfaces.IGeneDistance;

public class ClusterBenchmarkFactory {

    public static IClusterBenchmark create (BenchmarkType benchmarkType, IGeneDistance geneDistance, GeneClusterData goldStandard) {
        IClusterBenchmark clusterBenchmark = null;

        switch (benchmarkType) {
            case  Silhouette -> clusterBenchmark = new Silhouette(geneDistance);
            case Jaccard -> clusterBenchmark = new Jaccard(goldStandard);
            case Accuracy -> clusterBenchmark = new Accuracy(goldStandard);
            case NMI -> clusterBenchmark = new NMI(goldStandard);
            case MeanSquaredError -> throw new UnsupportedOperationException("Not supported yet.");
            case WCSS -> clusterBenchmark = new WCSS(geneDistance);
            default -> throw new UnsupportedOperationException("Select a valid benchmark");
        }

        return clusterBenchmark;
    }
}
