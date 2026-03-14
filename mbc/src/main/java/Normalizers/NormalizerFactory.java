package Normalizers;

import Interfaces.IDataNormalizer;
import java.util.List;

public class NormalizerFactory {
    public static IDataNormalizer createNormalizer(String normType) {
        return switch (normType.toLowerCase()) {
            case "irls" -> new IRLS();
            case "zscore" -> new ZScoreNormalizer();
            case "median", "medianratios" -> new MedianRatiosNormalization();
            case "pseudolog" -> new PseudologarithmNormalizer();
            case "countdist" -> new CountDistributionNormalizer();
            default -> throw new IllegalArgumentException("Unknown normalization type: " + normType);
        };
    }

    public static CompositeNormalizer createCompositeNormalizer(List<String> normTypes) {
        CompositeNormalizer composite = new CompositeNormalizer();
        if (normTypes == null || normTypes.isEmpty()) {
            composite.add(new IRLS()); // Default
            return composite;
        }
        for (String type : normTypes) {
            composite.add(createNormalizer(type));
        }
        return composite;
    }
}
