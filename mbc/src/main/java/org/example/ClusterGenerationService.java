package org.example;

import Common.GeneClusterData;
import Common.GeneExpressionData;
import FileDataOperations.GeneClusterDataWrite;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IGeneClusterDataWrite;

public class ClusterGenerationService {

    public static void RunClustering(GeneExpressionData geneExpressionData, IClusteringAlgorithm algorithm, String outputFilePrefix) {
        GeneClusterData result = algorithm.clusterGenes(geneExpressionData);

        IGeneClusterDataWrite geneExpressionDataWrite = new GeneClusterDataWrite();
        geneExpressionDataWrite.writeClusteringDataToFile(result, outputFilePrefix);
    }
}
