package ClusteringAlgorithms;

import Interfaces.IClusteringAlgorithm;
import Common.AlgorithmType;

public class ClusteringAlgorithmFactory     {
    public static IClusteringAlgorithm createClusteringAlgorithm(AlgorithmType algorithm) {
        switch (algorithm) {
            case Clust:
                return new ClustAlgorithm();
            default:
                throw new IllegalArgumentException("Unknown algorithm type");
        }
    }
}
