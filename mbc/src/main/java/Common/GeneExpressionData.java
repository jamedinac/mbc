package Common;

import java.util.HashMap;

public class GeneExpressionData {

    private final int numberOfGenes;
    private final double[][] expressionData;
    private final String[] geneId;
    private final String[] sampleIds;
    private final HashMap<String, SampleMetadata> metadata;
    private final int[] replicatesPerTime;
    private final int[] sampleTimeMap;
    private final String[] timeLabels;

    public GeneExpressionData(int numberOfGenes, double[][] expressionData, String[] geneId, String[] sampleIds, HashMap<String, SampleMetadata> metadata, int[] replicatesPerTime, int[] sampleTimeMap, String[] timeLabels) {
        this.numberOfGenes = numberOfGenes;
        this.expressionData = expressionData;
        this.geneId = geneId;
        this.sampleIds = sampleIds;
        this.metadata = metadata;
        this.replicatesPerTime = replicatesPerTime;
        this.sampleTimeMap = sampleTimeMap;
        this.timeLabels = timeLabels;
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

    public int[] getReplicatesPerTime() {
        return replicatesPerTime;
    }

    public int[] getSampleTimeMap() {
        return sampleTimeMap;
    }

    public int getNumberOfTimeSeries() {
        return timeLabels.length;
    }

    public String[] getTimeLabels() {
        return timeLabels;
    }

}
