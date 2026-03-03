package org.example;

import Common.ClusterBenchmarkResult;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import FileDataOperations.GeneClusterDataLoad;
import Interfaces.IClusterBenchmark;
import Interfaces.IClusterBenchmarkService;

public class ClusterBenchmarkService implements IClusterBenchmarkService {

    @Override
    public void runBenchmark(GeneExpressionData geneExpressionData, IClusterBenchmark clusterBenchmark, String outputFilePrefix) {
        GeneClusterDataLoad geneClusterDataLoad = new GeneClusterDataLoad(outputFilePrefix);
        GeneClusterData clusterData = geneClusterDataLoad.readClusterData();

        ClusterBenchmarkResult clusterBenchmarkResult = clusterBenchmark.evaluate(geneExpressionData, clusterData);
        clusterBenchmarkResult.writeClusterBenchmarkToFile(outputFilePrefix);
    }
}
