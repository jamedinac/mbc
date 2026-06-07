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

    /**
     * Determines the appropriate workflow type based on the requested normalization methods.
     *
     * @param normMethods The list of normalization methods requested by the user.
     * @return The determined WorkflowType.
     */
    public static WorkflowType determineFromConfig(List<String> normMethods) {
        if (normMethods == null || normMethods.isEmpty() || normMethods.contains("irls")) {
            return TRAC_GLM;
        }
        return STANDARD;
    }
}
