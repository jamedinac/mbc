package org.example;

import BenchmarkResult.ClusterBenchmarkResult;
import BenchmarkResult.CompositeBenchmarkResult;
import ClusterBenchmark.Accuracy;
import ClusterBenchmark.AdjustedRandIndex;
import ClusterBenchmark.CompositeBenchmark;
import ClusterBenchmark.Jaccard;
import ClusterBenchmark.NMI;
import ClusterBenchmark.Silhouette;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import FileDataOperations.BenchmarkResultsWriter;
import FileDataOperations.GeneClusterDataLoad;
import FileDataOperations.ProcessedDataLoad;
import GeneDistance.CorrelationDistance;
import GeneDistance.EuclideanDistance;
import Interfaces.IClusterBenchmark;
import Interfaces.IGeneDistance;
import Utilities.FileUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClusterBenchmarkService {

    private static final String outputFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\output.txt";
    private static final String goldStandardFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\ground_truth.txt";
    private static final String processedDataPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\processed_data.csv";

    public static void main(String[] args) {
        IGeneDistance geneDistance = new CorrelationDistance();

        GeneClusterDataLoad goldStandardLoader = new GeneClusterDataLoad(goldStandardFileName);
        GeneClusterData goldStandard = goldStandardLoader.readClusterData();
        
        CompositeBenchmark compositeBenchmark = new CompositeBenchmark();
        compositeBenchmark.addBenchmark(new Jaccard(goldStandard));
        compositeBenchmark.addBenchmark(new Silhouette(geneDistance));
        compositeBenchmark.addBenchmark(new Accuracy(goldStandard));
        compositeBenchmark.addBenchmark(new AdjustedRandIndex(goldStandard));
        compositeBenchmark.addBenchmark(new NMI(goldStandard));

        ClusterBenchmarkService benchmarkService = new ClusterBenchmarkService();
        benchmarkService.runBenchmark(processedDataPath, outputFileName, geneDistance, compositeBenchmark);
    }

    public void runBenchmark(String processedDataFilePath, String clusterDataFilePath, IGeneDistance geneDistance, IClusterBenchmark clusterBenchmark) {
        // Load data from files
        GeneClusterDataLoad clusterLoader = new GeneClusterDataLoad(clusterDataFilePath);
        GeneClusterData clusterData = clusterLoader.readClusterData();

        ProcessedDataLoad dataLoader = new ProcessedDataLoad();
        GeneExpressionData processedData = dataLoader.readProcessedData(processedDataFilePath);

        // Align processed data to only include genes present in the cluster data
        GeneExpressionData alignedData = alignProcessedData(processedData, clusterData);

        // Evaluate (injecting distance if needed is handled by the benchmark implementation or passed here)
        ClusterBenchmarkResult result = clusterBenchmark.evaluate(alignedData, clusterData);

        // Write results with _benchmarks suffix
        String benchmarkOutputPath = FileUtilities.appendSuffixToFileName(clusterDataFilePath, "_benchmarks");
        BenchmarkResultsWriter writer = new BenchmarkResultsWriter();
        writer.write((CompositeBenchmarkResult) result, benchmarkOutputPath);
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
                processedData.getTimeLabels()
        );
    }
}
