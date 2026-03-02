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
}
