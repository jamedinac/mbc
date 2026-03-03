package Interfaces;

import Common.ClusterBenchmarkResult;
import Common.GeneExpressionData;

public interface IClusterBenchmarkService {
    void runBenchmark(GeneExpressionData geneExpressionData, IClusterBenchmark clusterBenchmark, String outputFilePrefix);
}
