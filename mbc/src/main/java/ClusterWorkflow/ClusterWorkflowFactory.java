package ClusterWorkflow;

import Enum.WorkflowType;
import Interfaces.*;
import java.util.List;

/**
 * Factory for creating the appropriate ClusterWorkflow instance.
 */
public class ClusterWorkflowFactory {

    /**
     * Creates a ClusterWorkflow based on the provided configuration.
     *
     * @param type                     The type of workflow to instantiate.
     * @param enableProfiling          Whether to wrap the workflow in a profiling decorator.
     * @param dataLoad                 The data loading mechanism.
     * @param preNormalizationFilter   Filter applied to raw/normalized expression data.
     * @param postNormalizationFilter  Filter applied to statistical outputs (e.g., significance).
     * @param sampleFilter             The sample filtering mechanism.
     * @param algorithm                The clustering algorithm to apply.
     * @param normMethods              The list of normalization methods (used for Standard workflow).
     * @param compressionMethod        The replicate compression method (used for Standard workflow).
     * @return An instantiated and optionally decorated ClusterWorkflow.
     */
    public static IClusterWorkflow createWorkflow(
            WorkflowType type,
            boolean enableProfiling,
            IDataLoad dataLoad,
            IGeneFilter preNormalizationFilter,
            IGeneFilter postNormalizationFilter,
            ISampleFilter sampleFilter,
            IClusteringAlgorithm algorithm,
            List<String> normMethods,
            String compressionMethod) {

        IClusterWorkflow baseWorkflow = switch (type) {
            case TRAC_GLM -> {
                System.out.println("Using TracGLMWorkflow with Wald Test and FDR correction...");
                ILinearAlgebra mathAdapter = new MathAdapters.CommonsMathLinearAlgebra();
                IProbabilityProvider probAdapter = new MathAdapters.CommonsMathProbability();

                yield new TracGLMWorkflowI(
                        dataLoad,
                        preNormalizationFilter,
                        postNormalizationFilter,
                        sampleFilter,
                        new Normalizers.MedianRatiosNormalization(),
                        new ModelFitters.GLMProcessor(),
                        new Significance.WaldTestSignificanceTester(mathAdapter, probAdapter),
                        new Significance.BenjaminiHochbergCorrection(),
                        algorithm
                );
            }
            case STANDARD -> {
                IDataProcessor dataProcessor = new Common.DataProcessor(
                        preNormalizationFilter,
                        sampleFilter,
                        ReplicateCompression.ReplicateCompressionFactory.createReplicateCompression(compressionMethod),
                        Normalizers.NormalizerFactory.createCompositeNormalizer(normMethods)
                );

                yield new StandardIClusterWorkflow(dataLoad, dataProcessor, algorithm);
            }
        };

        if (enableProfiling) {
            System.out.println("Profiling enabled. Metrics will be saved to profile_metrics.json");
            return new ProfileIClusterWorkflowDecorator(baseWorkflow, new FileDataOperations.GsonDataWriter());
        }

        return baseWorkflow;
    }
}
