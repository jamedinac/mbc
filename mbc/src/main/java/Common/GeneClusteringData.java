package Common;

import java.io.Serializable;

public class GeneClusteringData {

    private final int numberOfGenes;
    private final int numberOfClusters;
    private final int[][] geneClusteringData;

    public GeneClusteringData(int numberOfGenes, int numberOfClusters, int[][] geneClusteringData) {
        this.numberOfGenes = numberOfGenes;
        this.numberOfClusters = numberOfClusters;
        this.geneClusteringData = geneClusteringData;
    }

    public int getNumberOfGenes() {
        return numberOfGenes;
    }

    public int getNumberOfClusters() {
        return numberOfClusters;
    }

    public int[][] getGeneClusteringData() {
        return geneClusteringData;
    }
}
