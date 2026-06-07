package MathAdapters;

import Interfaces.IProbabilityProvider;
import org.apache.commons.math3.distribution.NormalDistribution;

public class CommonsMathProbability implements IProbabilityProvider {
    private static final NormalDistribution STANDARD_NORMAL = new NormalDistribution(0, 1);

    @Override
    public double calculateTwoTailedPValue(double zScore) {
        return 2.0 * (1.0 - STANDARD_NORMAL.cumulativeProbability(Math.abs(zScore)));
    }
}