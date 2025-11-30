package Common;

public class GeneExpressionData {

    private final int numberOfGenes;
    private final int numberOfReplicates;
    private final int numberOfTimeSeries;
    private final int[][] expressionData;
    /*
     * TODO:
     *   private final int metadata []: identifica las replicas por tiempo
     */

    public GeneExpressionData(int numberOfGenes, int numberOfReplicates, int numberOfTimeSeries, int[][] expressionData) {
        this.numberOfGenes = numberOfGenes;
        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;
        this.expressionData = expressionData;
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
}
