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

    public static CompositeBenchmark createCompositeBenchmark(GeneClusterData goldStandard, IGeneDistance geneDistance, boolean includeInternalMetrics) {
        CompositeBenchmark compositeBenchmark = new CompositeBenchmark();
        
        // External validation metrics (always added)
        compositeBenchmark.addBenchmark(new Jaccard(goldStandard));
        compositeBenchmark.addBenchmark(new Accuracy(goldStandard));
        compositeBenchmark.addBenchmark(new AdjustedRandIndex(goldStandard));
        compositeBenchmark.addBenchmark(new NMI(goldStandard));
        
        // Internal validation metrics (require distance metric and processed data)
        if (includeInternalMetrics && geneDistance != null) {
            compositeBenchmark.addBenchmark(new Silhouette(geneDistance));
            compositeBenchmark.addBenchmark(new WCSS(geneDistance));
        }
        
        return compositeBenchmark;
    }
}

