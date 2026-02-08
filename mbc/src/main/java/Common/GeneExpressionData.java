package Common;

import java.util.HashMap;

public class GeneExpressionData {

    private final int numberOfGenes;
    private final int numberOfReplicates;
    private final int numberOfTimeSeries;
    private final double[][] expressionData;
    private final HashMap<String, SampleMetadata> metadata;
    private final String[] geneId;
    private final String[] geneColumnData;

    public GeneExpressionData(int numberOfGenes, int numberOfReplicates, int numberOfTimeSeries, double[][] expressionData, HashMap<String, SampleMetadata> metadata, String[] geneId, String[] geneColumnData) {
        this.numberOfGenes = numberOfGenes;
        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;
        this.expressionData = expressionData;
        this.metadata = metadata;
        this.geneId = geneId;
        this.geneColumnData = geneColumnData;
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
        return this.metadata.size();
    }

    public HashMap<String, SampleMetadata> getMetadata() {
        return metadata;
    }

    public double[] getGeneProfile(int geneIndex){
        return expressionData[geneIndex];
    }

    public String getGeneId(int gene) {
        return geneId[gene];
    }

    public String[] getGeneColumnData() {
        return geneColumnData;
    }
}
