package Common;

import java.util.ArrayList;

public class GeneExpressionData {

    private final int numberOfGenes;
    private final int numberOfReplicates;
    private final int numberOfTimeSeries;
    private final ArrayList<GeneProfile<Double>> expressionData;
    private final ArrayList<Integer> metadata;

    public GeneExpressionData(int numberOfGenes, int numberOfReplicates, int numberOfTimeSeries, ArrayList<GeneProfile<Double>> expressionData, ArrayList<Integer> metadata) {
        this.numberOfGenes = numberOfGenes;
        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;
        this.expressionData = expressionData;
        this.metadata = metadata;
    }

    public ArrayList<GeneProfile<Double>> getExpressionData() {
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

    public ArrayList<Integer> getMetadata() {
        return metadata;
    }

    public GeneProfile<Double> getGeneProfile(int geneIndex){
        return expressionData.get(geneIndex);
    }
}
