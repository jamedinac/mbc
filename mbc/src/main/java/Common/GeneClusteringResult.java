package Common;

import GeneProfile.DoubleGeneProfile;

import java.util.ArrayList;

public class GeneClusteringResult {

    private final int numberOfClusters;
    private final ArrayList<DoubleGeneProfile> geneClusteringData;
    private final GeneExpressionData geneExpressionData;

    public GeneClusteringResult(int numberOfClusters, ArrayList<DoubleGeneProfile> geneClusteringData, GeneExpressionData geneExpressionData) {
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

    public ArrayList<DoubleGeneProfile> getGeneClusteringData() {
        return geneClusteringData;
    }

    public GeneExpressionData getGeneExpressionData() {
        return geneExpressionData;
    }
}
