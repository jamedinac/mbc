package Interfaces;

public interface IGeneDistance {
    /**
     * Implements a distance definition between genes
      * @param geneA the first gene
     * @param geneB the second gene
     * @return the distance between genes
     */
    double getDistance(double[] geneA, double[] geneB);
}
