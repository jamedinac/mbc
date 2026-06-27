package Filter;

import Enum.FilterStatus;
import Interfaces.IGeneFilter;

public class GeneFilterByTotalExpression implements IGeneFilter {

    private final double expressionThreshold;

    public GeneFilterByTotalExpression(double expressionThreshold) {
        this.expressionThreshold = expressionThreshold;
    }

    @Override
    public FilterStatus filterGene(double[] geneExpressionRow) {
        double expressionSum = 0;

        for (double expression : geneExpressionRow) {
            expressionSum += expression;
        }

        return expressionSum > this.expressionThreshold ? FilterStatus.NOT_FILTERED : FilterStatus.TOTAL_EXPRESSION_FILTER;
    }
}
