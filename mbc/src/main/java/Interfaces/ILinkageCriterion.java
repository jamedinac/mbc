package Interfaces;

import Common.GeneExpressionData;
import java.util.List;

public interface ILinkageCriterion {
    double computeDistance(GeneExpressionData data, IGeneDistance geneDistance,
                           List<Integer> clusterA, List<Integer> clusterB);
}
