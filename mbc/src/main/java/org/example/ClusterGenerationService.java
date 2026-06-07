package org.example;

import ClusterWorkflow.ClusterWorkflowFactory;
import ClusteringAlgorithms.ClusterAlgorithmFactory;
import Common.WorkflowResult;
import Enum.WorkflowType;
import FileDataOperations.DataLoad;
import FileDataOperations.GeneClusterDataWrite;
import FileDataOperations.GeneExpressionDataWrite;
import Filter.FilterFactory;
import Filter.SampleFilterFactory;
import GeneDistance.DistanceFactory;
import Interfaces.ClusterWorkflow;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IDataLoad;
import Interfaces.IDataProcessor;
import Interfaces.IGeneClusterDataWrite;
import Interfaces.IGeneDistance;
import Interfaces.IGeneFilter;
import LinkageCriteria.LinkageFactory;
import Normalizers.NormalizerFactory;
import ReplicateCompression.ReplicateCompressionFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "ClusterGenerationService", mixinStandardHelpOptions = true, version = "1.0",
        description = "Gene Expression Trajectory Clustering Engine based on GLM Betas.")
public class ClusterGenerationService implements Callable<Integer> {

    @Parameters(index = "0", description = "Path to the count matrix file (e.g., counts.csv).")
    private File data;

    @Parameters(index = "1", description = "Path to the experimental design file (e.g., design.csv).")
    private File metadata;

    @Parameters(index = "2", description = "Destination path for the clustering results.")
    private String output;

    @Option(names = {"-a", "--algorithm"}, defaultValue = "hierarchical", description = "Clustering method (kmeans, hierarchical, dbscan, fcm). Default: hierarchical")
    private String algorithmStr;

    @Option(names = {"-k", "--clusters"}, defaultValue = "10", description = "Number of clusters (k). Default: 10")
    private Integer clusters;

    @Option(names = {"--eps"}, defaultValue = "0.5", description = "Epsilon parameter for DBSCAN. Default: 0.5")
    private Double eps;

    @Option(names = {"--minPts"}, defaultValue = "5", description = "Minimum points parameter for DBSCAN. Default: 5")
    private Integer minPts;

    @Option(names = {"-n", "--norm"}, split = ",", description = "Normalization methods to apply in order (irls,zscore,median,pseudolog,countdist). Default: irls")
    private List<String> norm;

    @Option(names = {"-f", "--filter"}, split = ",", description = "List of filters to apply (e.g., --filter non-zero,variance,1.0,total-expression,1.0)")
    private List<String> filters;

    @Option(names = {"-fs", "--filter-samples"}, split = ",", description = "List of sample traits to include (e.g., --filter-samples Condition,Treatment,Tissue,Liver)")
    private List<String> filterSamples;

    @Option(names = {"-c", "--compress"}, defaultValue = "default", description = "Replicate compression method (mean, variance, default).")
    private String compress;

    @Option(names = {"-d", "--distance"}, defaultValue = "correlation", description = "Distance metric (correlation, euclidean, jensenshannon).")
    private String distance;

    @Option(names = {"-l", "--linkage"}, defaultValue = "average", description = "Linkage criterion (average, complete, single).")
    private String linkage;

    @Option(names = {"-p", "--profile"}, description = "Enable profiling to record execution time and memory usage.")
    private boolean profile;

    @Option(names = {"-nf", "--no-filter"}, description = "Disable all gene filtering. This overrides the default filters and the --filter flag.")
    private boolean noFilter;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ClusterGenerationService()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        // Build gene filter
        IGeneFilter geneFilter = noFilter
                ? new Filter.CompositeFilter()
                : FilterFactory.createCompositeFilter(filters);

        // Data Load
        IDataLoad dataLoad = new DataLoad(data.getAbsolutePath(), metadata.getAbsolutePath(), "Time", "Sample");

        // Distance & Algorithm using factories
        IGeneDistance geneDistance = DistanceFactory.createDistance(distance);
        IClusteringAlgorithm algorithm = ClusterAlgorithmFactory.createAlgorithm(
                algorithmStr,
                clusters,
                eps,
                minPts,
                geneDistance,
                LinkageFactory.createLinkage(linkage)
        );

        // Workflow Assembly
        WorkflowType type = WorkflowType.determineFromConfig(norm);
        
        ClusterWorkflow workflow = ClusterWorkflowFactory.createWorkflow(
                type,
                profile,
                dataLoad,
                geneFilter,
                SampleFilterFactory.createSampleFilter(filterSamples),
                algorithm,
                norm,
                compress
        );

        // Execution
        System.out.println("Starting ClusterGenerationService workflow...");
        WorkflowResult result = workflow.execute();

        // Output results
        String processedDataPath = output + "_processed.csv";
        new GeneExpressionDataWrite().writeGeneExpressionDataToFile(result.getProcessedData(), processedDataPath);

        IGeneClusterDataWrite geneExpressionDataWrite = new GeneClusterDataWrite();
        geneExpressionDataWrite.writeClusteringDataToFile(result.getClusterData(), output);

        System.out.println("Clustering completed successfully.");
        return 0;
    }
}
