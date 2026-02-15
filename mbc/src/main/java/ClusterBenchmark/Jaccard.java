package ClusterBenchmark;

import Common.ClusterBenchmarkResult;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import Interfaces.IClusterBenchmark;

public class Jaccard implements IClusterBenchmark {

    GeneClusterData goldStandard;

    public Jaccard(GeneClusterData goldStandard) {
        this.goldStandard = goldStandard;
    }

    @Override
    public ClusterBenchmarkResult evaluate(GeneExpressionData geneExpressionData,  GeneClusterData geneClusterData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
