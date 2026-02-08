package Common;

public class GeneClusteringResult {

    private final int numberOfClusters;
    private final double[][] geneClusteringData;
    private final GeneExpressionData geneExpressionData;
    private final ClusteringAlgorithm clusteringAlgorithm;

    public GeneClusteringResult(int numberOfClusters, double[][] geneClusteringData, GeneExpressionData geneExpressionData, ClusteringAlgorithm clusteringAlgorithm) {
        this.numberOfClusters = numberOfClusters;
        this.geneClusteringData = geneClusteringData;
        this.geneExpressionData = geneExpressionData;
        this.clusteringAlgorithm = clusteringAlgorithm;
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

    public ClusteringAlgorithm getClusteringAlgorithm() {
        return clusteringAlgorithm;
    }
}
