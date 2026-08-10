package Normalizers;

import Enum.WorkflowType;
import Interfaces.IDataNormalizer;
import java.util.List;

public class NormalizerFactory {

    /**
     * Creates a standalone normalizer for the standard workflow.
     *
     * <p>Note that {@code trac-glm} is deliberately absent: it names a whole workflow
     * rather than a normalizer, and is resolved by
     * {@link WorkflowType#determineFromConfig(List)} before this factory is reached.
     * Any request for it here would mean the caller had already taken the wrong branch,
     * so it is rejected like any other unknown value.</p>
     */
    public static IDataNormalizer createNormalizer(String normType) {
        return switch (normType.toLowerCase()) {
            case "zscore" -> new ZScoreNormalizer();
            case "median", "medianratios" -> new MedianRatiosNormalization();
            case "pseudolog" -> new PseudologarithmNormalizer();
            case "countdist" -> new CountDistributionNormalizer();
            default -> throw new IllegalArgumentException("Unknown normalization type: " + normType
                    + ". Supported values are zscore, median, pseudolog and countdist; use '"
                    + WorkflowType.TRAC_GLM_NORM + "' (or omit --norm) for the TRaC-GLM workflow.");
        };
    }

    public static CompositeNormalizer createCompositeNormalizer(List<String> normTypes) {
        CompositeNormalizer composite = new CompositeNormalizer();
        // An empty list never reaches this factory: it selects the TRaC-GLM workflow.
        if (normTypes == null || normTypes.isEmpty()) {
            composite.add(new MedianRatiosNormalization());
            return composite;
        }
        for (String type : normTypes) {
            composite.add(createNormalizer(type));
        }
        return composite;
    }
}
