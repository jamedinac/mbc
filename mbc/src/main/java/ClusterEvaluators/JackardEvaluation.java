package ClusterEvaluators;

import Common.ClusterResult;
import Common.GeneClusteringData;
import Common.GeneExpressionData;
import Interfaces.IClusterEvaluation;

public class JackardEvaluation implements IClusterEvaluation {

    public ClusterResult evaluate(GeneClusteringData geneClusteringData, GeneExpressionData geneExpressionData) {
        // magic
        return new ClusterResult(true);
    }
}
