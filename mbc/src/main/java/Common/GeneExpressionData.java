package Common;

public class GeneExpressionData {

    private final int numberOfGenes;
    private final int numberOfSamples;
    private final int numberOfTimeSeries;
    private final int[][] expressionData;

    public GeneExpressionData(int numberOfGenes, int numberOfSamples, int numberOfTimeSeries, int[][] expressionData) {
        this.numberOfGenes = numberOfGenes;
        this.numberOfSamples = numberOfSamples;
        this.numberOfTimeSeries = numberOfTimeSeries;
        this.expressionData = expressionData;
    }

    public int[][] getExpressionData() {
        return expressionData;
    }

    public int getNumberOfGenes() {
        return numberOfGenes;
    }

    public int getNumberOfSamples() {
        return numberOfSamples;
    }

    public int getNumberOfTimeSeries() {
        return numberOfTimeSeries;
    }
}
