package Filter;

import Interfaces.IGeneFilter;

public class GeneFilterByTotalExpression implements IGeneFilter {

    private final double expressionThreshold;

    public GeneFilterByTotalExpression(double expressionThreshold) {
        this.expressionThreshold = expressionThreshold;
    }

    @Override
    public boolean filterGene(double[] geneExpressionRow) {
        double expressionSum = 0;

        for (int c = 1;  c < geneExpressionRow.length; c++) {
            expressionSum += geneExpressionRow[c];
        }

        return expressionSum > this.expressionThreshold;
    }
}
