package org.example;

import Common.*;
import FileDataOperations.GeneClusterDataLoad;

public class ClusterBenchmarkService {

    public static void RunBenchmark(ClusterBenchmarkInputData clusterBenchmarkInputData) {
        ClusterBenchmarkResult clusterBenchmarkResult = getClusterBenchmarkResult(clusterBenchmarkInputData);
        clusterBenchmarkResult.writeClusterBenchmarkToFile(clusterBenchmarkInputData.getDirectoryPath());
    }

    public static ClusterBenchmarkResult getClusterBenchmarkResult(ClusterBenchmarkInputData clusterBenchmarkInputData) {
        GeneExpressionData geneExpressionData = ClusterGenerationService.getGeneExpressionData(clusterBenchmarkInputData.getClusterGenerationInputData());

        GeneClusterDataLoad geneClusterDataLoad = new GeneClusterDataLoad(clusterBenchmarkInputData.getDirectoryPath(), clusterBenchmarkInputData.getOutputFileName());
        GeneClusterData clusterData = geneClusterDataLoad.readClusterData();

        return clusterBenchmarkInputData.getBenchmark().evaluate(geneExpressionData, clusterData);
    }
}
