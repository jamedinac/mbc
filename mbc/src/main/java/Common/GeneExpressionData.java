package Common;

import java.util.HashMap;

public class GeneExpressionData {

    private final int numberOfGenes;
    private final int numberOfReplicates;
    private final int numberOfTimeSeries;
    private final double[][] expressionData;
    private final String[] geneId;

    public GeneExpressionData(int numberOfGenes, int numberOfReplicates, int numberOfTimeSeries, double[][] expressionData, String[] geneId) {
        this.numberOfGenes = numberOfGenes;
        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;
        this.expressionData = expressionData;
        this.geneId = geneId;
    }

    public double[][] getExpressionData() {
        return expressionData;
    }

    public int getNumberOfGenes() {
        return numberOfGenes;
    }

    public int getNumberOfReplicates() {
        return numberOfReplicates;
    }

    public int getNumberOfTimeSeries() {
        return numberOfTimeSeries;
    }

    public int getNumberOfComponents() {
        return numberOfReplicates*numberOfTimeSeries;
    }

    public double[] getGeneProfile(int geneIndex){
        return expressionData[geneIndex];
    }

    public String getGeneId(int gene) {
        return geneId[gene];
    }

    public String[] getGeneIds() { return this.geneId; }

}
