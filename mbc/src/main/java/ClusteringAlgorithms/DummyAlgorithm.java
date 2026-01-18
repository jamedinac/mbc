package ClusteringAlgorithms;

import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import Interfaces.IClusteringAlgorithm;

public class DummyAlgorithm implements IClusteringAlgorithm {

    @Override
    public GeneClusteringResult clusterGenes(GeneExpressionData geneExpresionData) {
        int numberOfGenes = geneExpresionData.getNumberOfGenes();
        int numberOfClusters = geneExpresionData.getNumberOfGenes();

        double[][] geneClusteringData = new double[numberOfGenes][numberOfClusters];

        for (int i = 0; i < numberOfGenes; i++) {
            for (int j = 0; j < numberOfClusters; j++) {
                geneClusteringData[i][j] = i == j ? 1 : 0;
            }
        }

        return new GeneClusteringResult(numberOfClusters, geneClusteringData, geneExpresionData);
    }
}
