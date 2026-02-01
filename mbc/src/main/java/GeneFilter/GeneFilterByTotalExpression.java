package GeneFilter;

import Interfaces.IGeneFilter;

public class GeneFilterByTotalExpression implements IGeneFilter {

    private final int expressionThreshold;

    public GeneFilterByTotalExpression(int expressionThreshold) {
        this.expressionThreshold = expressionThreshold;
    }

    @Override
    public boolean filterGene(String geneExpressionRow) {
        int expressionSum = 0;

        String[] dataRow = geneExpressionRow.split("\t");
        for (int c = 1;  c < dataRow.length; c++) {
            expressionSum += Integer.parseInt(dataRow[c]);
        }

        return expressionSum > this.expressionThreshold;
    }
}
