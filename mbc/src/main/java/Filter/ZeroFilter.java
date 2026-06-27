package Filter;

import Enum.FilterStatus;
import Interfaces.IGeneFilter;

public class ZeroFilter implements IGeneFilter {
    @Override
    public FilterStatus filterGene(double[] geneExpressionRow) {
        for (double geneExpression : geneExpressionRow) {
            if (geneExpression == 0) {
                return FilterStatus.ZERO_FILTER;
            }
        }

        return FilterStatus.NOT_FILTERED;
    }
}
