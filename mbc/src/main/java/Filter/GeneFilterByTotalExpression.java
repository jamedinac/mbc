package Filter;

import Interfaces.IGeneFilter;

public class GeneFilterByTotalExpression implements IGeneFilter {

    private final int expressionThreshold;

    public GeneFilterByTotalExpression(int expressionThreshold) {
        this.expressionThreshold = expressionThreshold;
    }

    @Override
    public boolean filterGene(String[] geneExpressionRow) {
        int expressionSum = 0;

        for (int c = 1;  c < geneExpressionRow.length; c++) {
            expressionSum += Integer.parseInt(geneExpressionRow[c]);
        }

        return expressionSum > this.expressionThreshold;
    }
}
