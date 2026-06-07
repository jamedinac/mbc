package Common;

/**
 * Carries all statistical state out of the GLM Processor.
 *
 * @param betas         The estimated log2 fold changes.
 * @param weights       The diagonal of the weight matrix W for each gene.
 * @param priorVariance The ridge penalty lambda for each time point.
 * @param designMatrix  The design matrix X used in the GLM.
 * @param alphas        The estimated overdispersion parameters for each gene.
 */
public record GLMFitResult(
    double[][] betas,
    double[][] weights,
    double[] priorVariance,
    double[][] designMatrix,
    double[] alphas
) {}