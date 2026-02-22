package LinkageCriteria;

import Common.GeneExpressionData;
import Interfaces.IGeneDistance;
import Interfaces.ILinkageCriterion;
import java.util.List;

public class SingleLinkage implements ILinkageCriterion {

    @Override
    public double computeDistance(GeneExpressionData data, IGeneDistance geneDistance,
                                  List<Integer> clusterA, List<Integer> clusterB) {
        double minDistance = Double.MAX_VALUE;

        for (int geneA : clusterA) {
            for (int geneB : clusterB) {
                double distance = geneDistance.getDistance(
                        data.getGeneProfile(geneA), data.getGeneProfile(geneB));
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }

        return minDistance;
    }
}
