package org.example;

import Common.*;
import FileDataOperations.GeneClusterDataLoad;
import Interfaces.IClusterBenchmark;

public class ClusterBenchmarkService {

    public static void RunBenchmark(ClusterParameters clusterParameters, IClusterBenchmark clusterBenchmark) {
        ClusterBenchmarkResult clusterBenchmarkResult = getClusterBenchmarkResult(clusterParameters, clusterBenchmark);
        clusterBenchmarkResult.writeClusterBenchmarkToFile(clusterParameters.getOutputFilePrefix());
    }

    public static ClusterBenchmarkResult getClusterBenchmarkResult(ClusterParameters clusterParameters, IClusterBenchmark benchmark) {
        GeneExpressionData geneExpressionData = ClusterGenerationService.getGeneExpressionData(clusterParameters);

        GeneClusterDataLoad geneClusterDataLoad = new GeneClusterDataLoad(clusterParameters.getOutputFilePrefix());
        GeneClusterData clusterData = geneClusterDataLoad.readClusterData();

        return benchmark.evaluate(geneExpressionData, clusterData);
    }
}
