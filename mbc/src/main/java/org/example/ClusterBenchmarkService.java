package org.example;

import BenchmarkResult.ClusterBenchmarkResult;
import BenchmarkResult.CompositeBenchmarkResult;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import FileDataOperations.BenchmarkResultsWriter;
import FileDataOperations.GeneClusterDataLoad;
import FileDataOperations.ProcessedDataLoad;
import Interfaces.IClusterBenchmark;
import Interfaces.IClusterBenchmarkService;
import Interfaces.IGeneDistance;

public class ClusterBenchmarkService implements IClusterBenchmarkService {

    @Override
    public void runBenchmark(String processedDataFilePath, String clusterDataFilePath, IGeneDistance geneDistance, IClusterBenchmark clusterBenchmark, String outputFilePrefix) {
        // Load data from files
        GeneClusterDataLoad clusterLoader = new GeneClusterDataLoad(clusterDataFilePath);
        GeneClusterData clusterData = clusterLoader.readClusterData();

        ProcessedDataLoad dataLoader = new ProcessedDataLoad();
        GeneExpressionData processedData = dataLoader.readProcessedData(processedDataFilePath);

        // Evaluate (injecting distance if needed is handled by the benchmark implementation or passed here)
        ClusterBenchmarkResult result = clusterBenchmark.evaluate(processedData, clusterData);

        // Write results
        BenchmarkResultsWriter writer = new BenchmarkResultsWriter();
        writer.write((CompositeBenchmarkResult) result, outputFilePrefix);
    }
}
