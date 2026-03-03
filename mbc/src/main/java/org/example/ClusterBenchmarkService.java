package org.example;

import BenchmarkResult.ClusterBenchmarkResult;
import BenchmarkResult.CompositeBenchmarkResult;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import FileDataOperations.BenchmarkResultsWriter;
import FileDataOperations.GeneClusterDataLoad;
import Interfaces.IClusterBenchmark;
import Interfaces.IClusterBenchmarkService;

public class ClusterBenchmarkService implements IClusterBenchmarkService {

    @Override
    public void runBenchmark(GeneExpressionData geneExpressionData, IClusterBenchmark clusterBenchmark, String outputFilePrefix) {
        GeneClusterDataLoad geneClusterDataLoad = new GeneClusterDataLoad(outputFilePrefix);
        GeneClusterData clusterData = geneClusterDataLoad.readClusterData();

        ClusterBenchmarkResult clusterBenchmarkResult = clusterBenchmark.evaluate(geneExpressionData, clusterData);

        BenchmarkResultsWriter writer = new BenchmarkResultsWriter();
        writer.write((CompositeBenchmarkResult) clusterBenchmarkResult, outputFilePrefix);
    }
}
