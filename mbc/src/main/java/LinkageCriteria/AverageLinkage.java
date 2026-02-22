package LinkageCriteria;

import Common.GeneExpressionData;
import Interfaces.IGeneDistance;
import Interfaces.ILinkageCriterion;
import java.util.List;

public class AverageLinkage implements ILinkageCriterion {

    @Override
    public double computeDistance(GeneExpressionData data, IGeneDistance geneDistance,
                                  List<Integer> clusterA, List<Integer> clusterB) {
        double totalDistance = 0.0;

        for (int geneA : clusterA) {
            for (int geneB : clusterB) {
                totalDistance += geneDistance.getDistance(
                        data.getGeneProfile(geneA), data.getGeneProfile(geneB));
            }
        }

        return totalDistance / ((long) clusterA.size() * clusterB.size());
    }
}
