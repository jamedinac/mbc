package Interfaces;

public interface IGeneFilter {

    /**
     * Determines whether a gene should be filtered (included) or not
     * @param geneExpressionRow gene expresion values
     * @return boolean
     */
    boolean filterGene(String geneExpressionRow);
}
