package Common;

public class GeneClusteringResult {

    private final int numberOfGenes;
    private final int numberOfClusters;
    private final double[][] geneClusteringData;

    /*
     *  TODO:
     *   add reference to the source data
     */

    public GeneClusteringResult(int numberOfGenes, int numberOfClusters, double[][] geneClusteringData) {
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

    public double[][] getGeneClusteringData() {
        return geneClusteringData;
    }
}
