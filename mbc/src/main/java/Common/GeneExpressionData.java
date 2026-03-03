package Common;

import java.util.HashMap;

public class GeneExpressionData {

    private final int numberOfGenes;
    private final double[][] expressionData;
    private final String[] geneId;
    private final String[] sampleIds;
    private final HashMap<String, SampleMetadata> metadata;
    private final int numberOfReplicates;
    private final int numberOfTimeSeries;

    public GeneExpressionData(int numberOfGenes, double[][] expressionData, String[] geneId, String[] sampleIds, HashMap<String, SampleMetadata> metadata, int numberOfReplicates, int numberOfTimeSeries) {
        this.numberOfGenes = numberOfGenes;
        this.expressionData = expressionData;
        this.geneId = geneId;
        this.sampleIds = sampleIds;
        this.metadata = metadata;
        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;
    }

    public double[][] getExpressionData() {
        return expressionData;
    }

    public int getNumberOfGenes() {
        return numberOfGenes;
    }

    public int getNumberOfComponents() {
        return expressionData[0].length;
    }

    public double[] getGeneProfile(int geneIndex){
        return expressionData[geneIndex];
    }

    public String getGeneId(int gene) {
        return geneId[gene];
    }

    public String[] getGeneIds() { return this.geneId; }

    public String[] getSampleIds() {
        return sampleIds;
    }

    public HashMap<String, SampleMetadata> getMetadata() {
        return metadata;
    }

    public int getNumberOfReplicates() {
        return numberOfReplicates;
    }

    public int getNumberOfTimeSeries() {
        return numberOfTimeSeries;
    }

}
