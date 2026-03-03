package org.example;

import Common.ClusterBenchmarkResult;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import FileDataOperations.GeneClusterDataLoad;
import Interfaces.IClusterBenchmark;

public class ClusterBenchmarkService {

    public static void RunBenchmark(GeneExpressionData geneExpressionData, IClusterBenchmark clusterBenchmark, String outputFilePrefix) {
        ClusterBenchmarkResult clusterBenchmarkResult = getClusterBenchmarkResult(geneExpressionData, clusterBenchmark, outputFilePrefix);
        clusterBenchmarkResult.writeClusterBenchmarkToFile(outputFilePrefix);
    }

    public static ClusterBenchmarkResult getClusterBenchmarkResult(GeneExpressionData geneExpressionData, IClusterBenchmark benchmark, String outputFilePrefix) {
        GeneClusterDataLoad geneClusterDataLoad = new GeneClusterDataLoad(outputFilePrefix);
        GeneClusterData clusterData = geneClusterDataLoad.readClusterData();

        return benchmark.evaluate(geneExpressionData, clusterData);
    }
}
