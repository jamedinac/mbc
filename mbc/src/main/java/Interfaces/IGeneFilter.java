package Interfaces;

/**
 * Interface defining a condition to filter out insignificant genes from the dataset.
 */
public interface IGeneFilter {

    /**
     * Determines whether a gene should be retained (included) or filtered out based on its expression.
     * 
     * @param geneExpressionRow the expression values of a single gene across all samples
     * @return true if the gene should be kept, false if it should be removed
     */
    boolean filterGene(double[] geneExpressionRow);
}
