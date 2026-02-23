package ClusteringAlgorithms;

import Enum.ClusteringAlgorithmType;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IGeneDistance;
import Interfaces.ILinkageCriterion;

public class ClusterAlgorithmFactory {

    public static IClusteringAlgorithm getClusteringAlgorithm(ClusteringAlgorithmType clusteringAlgorithm, int k, int iterations, IGeneDistance geneDistance, ILinkageCriterion linkageCriterion, double eps, int minPts) {
        IClusteringAlgorithm algorithm = null;

        switch (clusteringAlgorithm) {
            case ClusteringAlgorithmType.KMeans -> algorithm = new KMeansAlgorithm(k, iterations, geneDistance);
            case ClusteringAlgorithmType.Hierarchical -> algorithm = new HierachicalClusteringAlgorithm(k, geneDistance, linkageCriterion);
            case ClusteringAlgorithmType.DBSCAN -> algorithm = new DBSCANAlgorithm(eps, minPts, geneDistance);
            default -> throw new UnsupportedOperationException("Select a valid clustering algorithm");
        }

        return algorithm;
    }
}
