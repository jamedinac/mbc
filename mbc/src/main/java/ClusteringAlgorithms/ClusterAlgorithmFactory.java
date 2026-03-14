package ClusteringAlgorithms;

import Interfaces.IClusteringAlgorithm;
import Interfaces.IGeneDistance;
import Interfaces.ILinkageCriterion;

public class ClusterAlgorithmFactory {

    public static IClusteringAlgorithm createKMeans(int k, int maxIterations, IGeneDistance geneDistance) {
        return new KMeansAlgorithm(k, maxIterations, geneDistance);
    }

    public static IClusteringAlgorithm createHierarchical(int k, IGeneDistance geneDistance, ILinkageCriterion linkageCriterion) {
        return new HierachicalClusteringAlgorithm(k, geneDistance, linkageCriterion);
    }

    public static IClusteringAlgorithm createDBSCAN(double eps, int minPts, IGeneDistance geneDistance) {
        return new DBSCANAlgorithm(eps, minPts, geneDistance);
    }

    public static IClusteringAlgorithm createFuzzyCMeans(int k, double m, int maxIterations, double epsilon, IGeneDistance geneDistance) {
        return new FuzzyCMeansAlgorithm(k, m, maxIterations, epsilon, geneDistance);
    }

    public static IClusteringAlgorithm createAlgorithm(String type, int k, IGeneDistance dist, ILinkageCriterion linkage) {
        return switch (type.toLowerCase()) {
            case "kmeans" -> createKMeans(k, 1000, dist);
            case "dbscan" -> createDBSCAN(0.5, 5, dist);
            case "fcm" -> createFuzzyCMeans(k, 2.0, 1000, 1e-4, dist);
            case "hierarchical" -> createHierarchical(k, dist, linkage);
            default -> throw new IllegalArgumentException("Unknown algorithm: " + type);
        };
    }
}
