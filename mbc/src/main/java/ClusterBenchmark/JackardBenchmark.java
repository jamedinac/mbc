package ClusterBenchmark;

import Common.ClusterResult;
import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import Interfaces.IClusterBenchmark;

public class JackardBenchmark implements IClusterBenchmark {

    public ClusterResult evaluate(GeneClusteringResult geneClusteringData, GeneExpressionData geneExpressionData) {
        // magic
        return new ClusterResult(true);
    }
}
