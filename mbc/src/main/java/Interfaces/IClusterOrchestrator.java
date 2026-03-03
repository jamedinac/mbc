package Interfaces;

import Common.GeneExpressionData;

public interface IClusterOrchestrator {
    void executePipeline(GeneExpressionData rawData, IClusteringAlgorithm algorithm, IClusterBenchmark benchmark, String outputFilePrefix);
}
