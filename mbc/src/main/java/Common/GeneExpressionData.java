package Common;

public class GeneExpressionData {

    private final int numberOfGenes;
    private final double[][] expressionData;
    private final String[] geneId;

    public GeneExpressionData(int numberOfGenes, double[][] expressionData, String[] geneId) {
        this.numberOfGenes = numberOfGenes;
        this.expressionData = expressionData;
        this.geneId = geneId;
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

}
