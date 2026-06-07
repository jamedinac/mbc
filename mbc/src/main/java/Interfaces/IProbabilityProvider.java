package Interfaces;

/**
 * Abstraction layer for statistical probability distributions.
 * This interface isolates the core statistical calculations from specific external math libraries.
 */
public interface IProbabilityProvider {
    
    /**
     * Computes the two-tailed P-value for a given Z-score, 
     * typically based on the Standard Normal Distribution.
     *
     * @param zScore The calculated Z-score from a statistical test (e.g., Wald Test).
     * @return The two-tailed P-value representing the probability of observing such a Z-score.
     */
    double calculateTwoTailedPValue(double zScore);
}
