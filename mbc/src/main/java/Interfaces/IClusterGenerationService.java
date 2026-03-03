package Interfaces;

import Common.GeneExpressionData;

public interface IClusterGenerationService {
    void runClustering(GeneExpressionData geneExpressionData, IClusteringAlgorithm algorithm, String outputFilePrefix);
}
