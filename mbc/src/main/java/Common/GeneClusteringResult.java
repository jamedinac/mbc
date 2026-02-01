package Common;

import java.util.ArrayList;

public class GeneClusteringResult {

    private final int numberOfClusters;
    private final double[][] geneClusteringData;
    private final GeneExpressionData geneExpressionData;

    public GeneClusteringResult(int numberOfClusters, double[][] geneClusteringData, GeneExpressionData geneExpressionData) {
        this.numberOfClusters = numberOfClusters;
        this.geneClusteringData = geneClusteringData;
        this.geneExpressionData = geneExpressionData;
    }

    public int getNumberOfGenes() {
        return geneExpressionData.getNumberOfGenes();
    }

    public int getNumberOfClusters() {
        return numberOfClusters;
    }

    public double[][] getGeneClusteringData() {
        return geneClusteringData;
    }

    public GeneExpressionData getGeneExpressionData() {
        return geneExpressionData;
    }
}
