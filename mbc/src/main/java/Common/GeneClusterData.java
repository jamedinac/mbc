package Common;

public class GeneClusterData {

    private final int numberOfClusters;
    private final int numberOfGenes;
    private final String[] geneIds;
    private final double[][] clusteringData;

    public GeneClusterData(int numberOfGenes, int numberOfClusters, String[] geneIds, double[][] clusteringData) {
        this.numberOfGenes = numberOfGenes;
        this.numberOfClusters = numberOfClusters;
        this.geneIds = geneIds;
        this.clusteringData = clusteringData;
    }

    public int getNumberOfClusters() {
        return numberOfClusters;
    }

    public int getNumberOfGenes() {
        return numberOfGenes;
    }

    public String[] getGeneIds() {
        return geneIds;
    }

    public String getGeneId(int id) {
        return geneIds[id];
    }

    public double[][] getClusteringData() {
        return clusteringData;
    }

}
