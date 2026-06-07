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
     * @param type              The type of workflow to instantiate.
     * @param enableProfiling   Whether to wrap the workflow in a profiling decorator.
     * @param dataLoad          The data loading mechanism.
     * @param geneFilter        The gene filtering mechanism.
     * @param sampleFilter      The sample filtering mechanism.
     * @param algorithm         The clustering algorithm to apply.
     * @param normMethods       The list of normalization methods (used for Standard workflow).
     * @param compressionMethod The replicate compression method (used for Standard workflow).
     * @return An instantiated and optionally decorated ClusterWorkflow.
     */
    public static ClusterWorkflow createWorkflow(
            WorkflowType type,
            boolean enableProfiling,
            IDataLoad dataLoad,
            IGeneFilter geneFilter,
            ISampleFilter sampleFilter,
            IClusteringAlgorithm algorithm,
            List<String> normMethods,
            String compressionMethod) {

        ClusterWorkflow baseWorkflow = switch (type) {
            case TRAC_GLM -> {
                System.out.println("Using TracGLMWorkflow with Wald Test and FDR correction...");
                ILinearAlgebra mathAdapter = new MathAdapters.CommonsMathLinearAlgebra();
                IProbabilityProvider probAdapter = new MathAdapters.CommonsMathProbability();

                yield new TracGLMWorkflow(
                        dataLoad,
                        geneFilter,
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
                        geneFilter,
                        sampleFilter,
                        ReplicateCompression.ReplicateCompressionFactory.createReplicateCompression(compressionMethod),
                        Normalizers.NormalizerFactory.createCompositeNormalizer(normMethods)
                );

                yield new StandardClusterWorkflow(dataLoad, dataProcessor, algorithm);
            }
        };

        if (enableProfiling) {
            System.out.println("Profiling enabled. Metrics will be saved to profile_metrics.txt");
            return new ProfileClusterWorkflowDecorator(baseWorkflow);
        }

        return baseWorkflow;
    }
}
