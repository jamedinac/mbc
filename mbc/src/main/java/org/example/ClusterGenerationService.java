package org.example;

import ClusterWorkflow.ClusterWorkflowFactory;
import ClusteringAlgorithms.ClusterAlgorithmFactory;
import Common.GeneClusterData;
import Common.WorkflowResult;
import Enum.WorkflowType;
import FileDataOperations.DataLoad;
import FileDataOperations.GeneClusterDataWrite;
import FileDataOperations.GeneExpressionDataWrite;
import Filter.FilterFactory;
import Filter.SampleFilterFactory;
import GeneDistance.DistanceFactory;
import Interfaces.IClusterWorkflow;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IDataLoad;
import Interfaces.IGeneClusterDataWrite;
import Interfaces.IGeneDistance;
import LinkageCriteria.LinkageFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "ClusterGenerationService", mixinStandardHelpOptions = true, version = "2.0.0-beta",
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

    @Option(names = {"-n", "--norm"}, split = ",", description = "Workflow/normalization selector. Use trac-glm (default) for the GLM + Wald + FDR workflow, or one or more of zscore,median,pseudolog,countdist for the standard workflow. Default: trac-glm")
    private List<String> norm;

    @Option(names = {"-f", "--filter"}, split = ",", description = "List of filters to apply (e.g., --filter non-zero,variance,1.0,total-expression,1.0,significance,0.05)")
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



    public static void main(String[] args) {
        int exitCode = new CommandLine(new ClusterGenerationService()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        // Build gene filters (Pre and Post Normalization)
        FilterFactory.FilterSetup filterSetup = FilterFactory.createFilterSetup(filters);

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
        
        IClusterWorkflow workflow = ClusterWorkflowFactory.createWorkflow(
                type,
                profile,
                dataLoad,
                filterSetup.preNormalization(),
                filterSetup.postNormalization(),
                SampleFilterFactory.createSampleFilter(filterSamples),
                algorithm,
                norm,
                compress
        );

        // Execution
        System.out.println("Starting ClusterGenerationService workflow...");
        WorkflowResult result = workflow.execute();

        // Output results
        String processedDataPath = output + "_normalized_data.csv";
        new GeneExpressionDataWrite().writeGeneExpressionDataToFile(result.getProcessedData(), processedDataPath);

        // Merge filtered-out genes as an additional cluster (K+1)
        GeneClusterData mergedClusterData = Utilities.ClusterDataMerger.mergeFilteredGenes(
                result.getClusterData(), result.getFilteredOutGenes());

        IGeneClusterDataWrite geneExpressionDataWrite = new GeneClusterDataWrite();
        geneExpressionDataWrite.writeClusteringDataToFile(mergedClusterData, output);

        System.out.println("Clustering completed successfully.");
        return 0;
    }
}
