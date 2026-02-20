package org.example;

import Common.*;
import FileDataOperations.GeneClusterDataLoad;

public class ClusterBenchmarkService {

    public static void RunBenchmark(ClusterParameters clusterParameters) {
        ClusterBenchmarkResult clusterBenchmarkResult = getClusterBenchmarkResult(clusterParameters);
        clusterBenchmarkResult.writeClusterBenchmarkToFile(clusterParameters.getOutputFileName());
    }

    public static ClusterBenchmarkResult getClusterBenchmarkResult(ClusterParameters clusterParameters) {
        GeneExpressionData geneExpressionData = ClusterGenerationService.getGeneExpressionData(clusterParameters);

        GeneClusterDataLoad geneClusterDataLoad = new GeneClusterDataLoad(clusterBenchmarkInputData.getDirectoryPath(), clusterBenchmarkInputData.getOutputFileName());
        GeneClusterData clusterData = geneClusterDataLoad.readClusterData();

        return clusterBenchmarkInputData.getBenchmark().evaluate(geneExpressionData, clusterData);
    }
}
