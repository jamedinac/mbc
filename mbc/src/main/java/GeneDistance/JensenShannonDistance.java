package GeneDistance;

import Interfaces.IGeneDistance;

/**
 * Jensen-Shannon distance, the square root of the Jensen-Shannon divergence.
 *
 * <p>This metric is only defined over non-negative vectors, so it suits count-like or
 * probability-like profiles (see {@code countdist} normalization). It is <b>not</b>
 * applicable to the GLM beta coefficients produced by the TRaC-GLM workflow, which are
 * log fold changes and routinely negative.</p>
 */
public class JensenShannonDistance implements IGeneDistance {
    @Override
    public double getDistance(double[] P, double[] Q) {
        this.requireNonNegative(P);
        this.requireNonNegative(Q);

        double[] mean = this.getMean(P, Q);
        double jsd = this.getKullbackLeiblerDivergence(P, mean) + this.getKullbackLeiblerDivergence(Q, mean);
        return Math.sqrt(Math.max(jsd, 0.0) / 2.0);
    }

    /**
     * Rejects negative components instead of silently skipping them.
     *
     * <p>The divergence terms below only accumulate strictly positive components, so a
     * profile containing negative values would quietly produce a partial - and possibly
     * negative - divergence, whose square root is NaN. Failing here makes the
     * misapplication visible rather than turning every distance into NaN.</p>
     */
    private void requireNonNegative(double[] profile) {
        for (int i = 0; i < profile.length; i++) {
            if (profile[i] < 0 || Double.isNaN(profile[i])) {
                throw new IllegalArgumentException(
                        "Jensen-Shannon distance requires non-negative profiles, but component " + i
                                + " is " + profile[i] + ". This metric cannot be applied to GLM beta "
                                + "coefficients; use correlation or euclidean instead.");
            }
        }
    }

    private double getKullbackLeiblerDivergence(double[] p, double[] q) {
        double result = 0;
        for (int i = 0; i < p.length; i++) {
            if (p[i] > 0 && q[i] > 0) {
                result += p[i] * Math.log(p[i] / q[i]);
            }
        }
        return result;
    }

    private double[] getMean(double[] p, double[] q) {
        double[] result = new double[p.length];
        for (int i = 0; i < p.length; i++) {
            result[i] = (p[i] + q[i]) / 2.0;
        }
        return result;
    }
}
