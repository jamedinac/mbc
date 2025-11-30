package Common;

public class GeneExpressionData {

    private final int numberOfGenes;
    private final int numberOfReplicates;
    private final int numberOfTimeSeries;
    private final int[][] expressionData;
    private final int[] metadata;

    public GeneExpressionData(int numberOfGenes, int numberOfReplicates, int numberOfTimeSeries, int[][] expressionData, int[] metadata) {
        this.numberOfGenes = numberOfGenes;
        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;
        this.expressionData = expressionData;
        this.metadata = metadata;
    }

    public int[][] getExpressionData() {
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

    public int[] getMetadata() {
        return metadata;
    }
}
