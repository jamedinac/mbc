package Enum;

import java.util.List;

/**
 * Defines the available types of clustering workflows.
 */
public enum WorkflowType {
    /**
     * The standard clustering workflow using basic normalization and replicate compression.
     */
    STANDARD,

    /**
     * The significance-driven clustering workflow utilizing Generalized Linear Models (GLM), 
     * Wald Testing, and FDR correction.
     */
    TRAC_GLM;

    /** The {@code --norm} value that selects the significance-driven TRaC-GLM workflow. */
    public static final String TRAC_GLM_NORM = "trac-glm";

    /**
     * Determines the appropriate workflow type based on the requested normalization methods.
     *
     * @param normMethods The list of normalization methods requested by the user.
     * @return TRAC_GLM when no method is given or {@value #TRAC_GLM_NORM} is among them,
     *         STANDARD otherwise.
     */
    public static WorkflowType determineFromConfig(List<String> normMethods) {
        if (normMethods == null || normMethods.isEmpty()) {
            return TRAC_GLM;
        }

        // Matched case-insensitively so that "-n TRaC-GLM" behaves like "-n trac-glm";
        // NormalizerFactory already lowercases its own lookups.
        for (String normMethod : normMethods) {
            if (normMethod != null && normMethod.trim().equalsIgnoreCase(TRAC_GLM_NORM)) {
                return TRAC_GLM;
            }
        }

        return STANDARD;
    }
}
