package Interfaces;

import Common.GeneExpressionData;

public interface IClusterOrchestrator {
    void executePipeline(GeneExpressionData rawData, String processedDataPath, IClusteringAlgorithm algorithm, IGeneDistance geneDistance, IClusterBenchmark benchmark, String outputFilePrefix);
}
