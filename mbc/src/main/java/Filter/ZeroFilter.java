package Filter;

import Interfaces.IGeneFilter;

public class ZeroFilter implements IGeneFilter {
    @Override
    public boolean filterGene(double[] geneExpressionRow) {
        for (double geneExpression : geneExpressionRow) {
            if (geneExpression == 0) {
                return false;
            }
        }
        return true;
    }
}
