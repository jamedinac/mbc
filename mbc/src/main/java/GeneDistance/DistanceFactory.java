package GeneDistance;

import Interfaces.IGeneDistance;

public class DistanceFactory {
    public static IGeneDistance createDistance(String distanceType) {
        return switch (distanceType.toLowerCase()) {
            case "correlation" -> new CorrelationDistance();
            case "euclidean" -> new EuclideanDistance();
            case "jensenshannon" -> new JensenShannonDistance();
            default -> throw new IllegalArgumentException("Unknown distance metric: " + distanceType);
        };
    }
}
