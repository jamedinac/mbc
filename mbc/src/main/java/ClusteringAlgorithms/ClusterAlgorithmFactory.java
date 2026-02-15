package ClusteringAlgorithms;

import Common.ClusteringAlgorithmType;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IGeneDistance;

public class ClusterAlgorithmFactory {

    public static IClusteringAlgorithm getClusteringAlgorithm(ClusteringAlgorithmType clusteringAlgorithm, int k, int iterations, IGeneDistance geneDistance) {
        IClusteringAlgorithm algorithm = null;

        switch (clusteringAlgorithm) {
            case ClusteringAlgorithmType.KMeans -> algorithm = new KMeansAlgorithm(k, iterations, geneDistance);
            default -> throw new UnsupportedOperationException("Select a valid benchmark");
        }

        return algorithm;
    }
}
