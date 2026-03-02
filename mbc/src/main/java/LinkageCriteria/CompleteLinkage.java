package LinkageCriteria;

import Common.GeneExpressionData;
import Interfaces.IGeneDistance;
import Interfaces.ILinkageCriterion;
import java.util.List;

public class CompleteLinkage implements ILinkageCriterion {

    @Override
    public double computeDistance(GeneExpressionData data, IGeneDistance geneDistance,
                                  List<Integer> clusterA, List<Integer> clusterB) {
        double maxDistance = -Double.MAX_VALUE;

        for (int geneA : clusterA) {
            for (int geneB : clusterB) {
                double distance = geneDistance.getDistance(
                        data.getGeneProfile(geneA), data.getGeneProfile(geneB));
                if (distance > maxDistance) {
                    maxDistance = distance;
                }
            }
        }

        return maxDistance;
    }
}
