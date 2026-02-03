package Filter;

import Interfaces.IGeneFilter;

public class GeneFilterByVariance implements IGeneFilter {

    private final double varianceThreshold;

    public GeneFilterByVariance(double varianceThreshold) {
        this.varianceThreshold = varianceThreshold;
    }

    @Override
    public boolean filterGene(String[] geneExpressionRow) {
        double mean = 0.0;
        double variance = 0.0;

        for (int c = 1; c < geneExpressionRow.length; c++) {
            mean += Double.parseDouble(geneExpressionRow[c]);
        }
        mean /= geneExpressionRow.length;

        for (int c = 1; c < geneExpressionRow.length; c++) {
            double difference = Double.parseDouble(geneExpressionRow[c]) - mean;
            variance += difference * difference;
        }
        variance /= geneExpressionRow.length - 1;

        return variance > this.varianceThreshold;
    }
}
