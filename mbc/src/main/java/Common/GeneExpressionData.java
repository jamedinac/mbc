package Common;

import java.util.ArrayList;

public class GeneExpressionData {

    private final int numberOfGenes;
    private final int numberOfReplicates;
    private final int numberOfTimeSeries;
    private final double[][] expressionData;
    private final String[] metadata;
    private final String[] geneId;

    public GeneExpressionData(int numberOfGenes,
                              int numberOfReplicates,
                              int numberOfTimeSeries,
                              double[][] expressionData,
                              String[] metadata,
                              String[] geneId) {
        this.numberOfGenes = numberOfGenes;
        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;
        this.expressionData = expressionData;
        this.metadata = metadata;
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
        return this.numberOfReplicates * this.numberOfTimeSeries;
    }

    public String[] getMetadata() {
        return metadata;
    }

    public double[] getGeneProfile(int geneIndex){
        return expressionData[geneIndex];
    }

    public String getGeneId(int gene) {
        return geneId[gene];
    }
}
