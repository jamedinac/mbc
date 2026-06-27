package Filter;

import Enum.FilterStatus;
import Interfaces.IGeneFilter;

/**
 * Filter that evaluates genes based on their statistical significance (p-values).
 * This filter expects a double array of p-values (usually adjusted p-values) 
 * and retains genes where at least one p-value is below the threshold.
 */
public class SignificanceFilter implements IGeneFilter {
    
    private final double threshold;

    /**
     * Constructs a significance filter with the given alpha threshold.
     *
     * @param threshold The alpha significance threshold (e.g., 0.05).
     */
    public SignificanceFilter(double threshold) {
        this.threshold = threshold;
    }

    /**
     * Determines whether a gene should be retained based on its p-values.
     * 
     * @param pValues An array of p-values for a single gene across time points.
     * @return {@link FilterStatus#NOT_FILTERED} if at least one p-value is below the threshold, or {@link FilterStatus#SIGNIFICANCE_FILTER} otherwise.
     */
    @Override
    public FilterStatus filterGene(double[] pValues) {
        for (double p : pValues) {
            if (p < threshold) {
                return FilterStatus.NOT_FILTERED;
            }
        }
        return FilterStatus.SIGNIFICANCE_FILTER;
    }
}
