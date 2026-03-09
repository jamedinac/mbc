package Interfaces;

/**
 * Interface representing a mathematical distance metric between two genes' expression profiles.
 */
public interface IGeneDistance {

    /**
     * Calculates the distance between two genes based on their expression profiles.
     * 
     * @param geneA the expression profile array of the first gene
     * @param geneB the expression profile array of the second gene
     * @return the calculated distance between the two genes
     */
    double getDistance(double[] geneA, double[] geneB);
}
