package org.example;

import BenchmarkResult.ClusterBenchmarkResult;
import BenchmarkResult.CompositeBenchmarkResult;
import ClusterBenchmark.ClusterBenchmarkFactory;
import ClusterBenchmark.CompositeBenchmark;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import FileDataOperations.BenchmarkResultsWriter;
import FileDataOperations.GeneClusterDataLoad;
import FileDataOperations.ProcessedDataLoad;
import GeneDistance.DistanceFactory;
import Interfaces.IClusterBenchmark;
import Interfaces.IGeneDistance;
import Utilities.FileUtilities;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

@Command(name = "ClusterBenchmarkService", mixinStandardHelpOptions = true, version = "1.0", description = "Evaluates clustering results against a gold standard and optionally calculates internal metrics.")
public class ClusterBenchmarkService implements Callable<Integer> {

    @Parameters(index = "0", description = "Path to the generated clustering results file.")
    private File clusterDataFile;

    @Parameters(index = "1", description = "Path to the gold standard (ground truth) cluster file.")
    private File goldStandardFile;

    @Option(names = { "-nd",
            "--normalized-data" }, description = "Path to the normalized gene expression matrix (CSV). Required for internal metrics.")
    private File normalizedDataFile;

    @Option(names = { "-d",
            "--distance" }, defaultValue = "correlation", description = "Distance metric (correlation, euclidean, jensenshannon). Default: correlation")
    private String distanceStr;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ClusterBenchmarkService()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Starting ClusterBenchmarkService...");

        // Load Gold Standard
        GeneClusterDataLoad goldStandardLoader = new GeneClusterDataLoad(goldStandardFile.getAbsolutePath());
        GeneClusterData goldStandard = goldStandardLoader.readClusterData();

        // Determine if internal metrics should be calculated
        boolean includeInternalMetrics = (normalizedDataFile != null);
        IGeneDistance geneDistance = null;

        if (includeInternalMetrics) {
            geneDistance = DistanceFactory.createDistance(distanceStr);
            System.out.println("Including internal metrics using distance: " + distanceStr);
        } else {
            System.out.println("Normalized data not provided. Calculating external metrics only.");
        }

        // Build Benchmark Suite using Factory
        CompositeBenchmark compositeBenchmark = ClusterBenchmarkFactory.createCompositeBenchmark(goldStandard,
                geneDistance, includeInternalMetrics);

        // Run Benchmark
        runBenchmark(clusterDataFile.getAbsolutePath(), geneDistance, compositeBenchmark, includeInternalMetrics);

        return 0;
    }

    private void runBenchmark(String clusterDataFilePath, IGeneDistance geneDistance,
            IClusterBenchmark clusterBenchmark, boolean includeInternalMetrics) {
        // Load generated cluster data
        GeneClusterDataLoad clusterLoader = new GeneClusterDataLoad(clusterDataFilePath);
        GeneClusterData clusterData = clusterLoader.readClusterData();

        GeneExpressionData alignedData = null;

        if (includeInternalMetrics && normalizedDataFile != null) {
            ProcessedDataLoad dataLoader = new ProcessedDataLoad();
            GeneExpressionData processedData = dataLoader.readProcessedData(normalizedDataFile.getAbsolutePath());
            alignedData = alignProcessedData(processedData, clusterData);
        } else {
            // Create a dummy GeneExpressionData or handle null inside evaluate if required
            // by the interface contract.
            // Based on current implementation, some external metrics don't use it, but the
            // interface requires it.
            // Passing null here assuming external metrics ignore it.
        }

        // Evaluate
        System.out.println("Evaluating metrics...");
        ClusterBenchmarkResult result = clusterBenchmark.evaluate(alignedData, clusterData);

        // Write results
        String benchmarkOutputPath = FileUtilities.appendSuffixToFileName(clusterDataFilePath, "_benchmarks");
        if (benchmarkOutputPath.contains(".")) {
            benchmarkOutputPath = benchmarkOutputPath.substring(0, benchmarkOutputPath.lastIndexOf('.')) + ".json";
        } else {
            benchmarkOutputPath += ".json";
        }
        
        BenchmarkResultsWriter writer = new BenchmarkResultsWriter(new FileDataOperations.GsonDataWriter());
        writer.write((CompositeBenchmarkResult) result, benchmarkOutputPath);
        System.out.println("Benchmarking completed successfully. Results saved to: " + benchmarkOutputPath);
    }

    private GeneExpressionData alignProcessedData(GeneExpressionData processedData, GeneClusterData clusterData) {
        Set<String> validGenes = new HashSet<>(Arrays.asList(clusterData.getGeneIds()));

        List<double[]> alignedExpressionList = new ArrayList<>();
        List<String> alignedGeneIds = new ArrayList<>();

        for (int i = 0; i < processedData.getNumberOfGenes(); i++) {
            String geneId = processedData.getGeneId(i);
            if (validGenes.contains(geneId)) {
                alignedGeneIds.add(geneId);
                alignedExpressionList.add(processedData.getGeneProfile(i));
            }
        }

        double[][] alignedExpressionData = new double[alignedExpressionList.size()][];
        alignedExpressionList.toArray(alignedExpressionData);

        return new GeneExpressionData(
                alignedExpressionList.size(),
                alignedExpressionData,
                alignedGeneIds.toArray(new String[0]),
                processedData.getSampleIds(),
                processedData.getMetadata(),
                processedData.getReplicatesPerTime(),
                processedData.getSampleTimeMap(),
                processedData.getTimeLabels());
    }
}
