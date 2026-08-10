package MathAdapters;

import Interfaces.IProbabilityProvider;
import org.apache.commons.math3.distribution.NormalDistribution;

public class CommonsMathProbability implements IProbabilityProvider {
    private static final NormalDistribution STANDARD_NORMAL = new NormalDistribution(0, 1);

    /**
     * Two-tailed p-value of a z score, evaluated in the lower tail.
     *
     * <p>The equivalent upper-tail form 2 * (1 - cdf(|z|)) cancels catastrophically and
     * underflows to exactly 0 once |z| exceeds roughly 8.2. Evaluating cdf(-|z|) keeps
     * the small p-values that FDR correction depends on.</p>
     */
    @Override
    public double calculateTwoTailedPValue(double zScore) {
        if (Double.isNaN(zScore)) {
            return 1.0;
        }
        return 2.0 * STANDARD_NORMAL.cumulativeProbability(-Math.abs(zScore));
    }
}