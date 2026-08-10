package ReplicateCompression;

import java.util.ArrayList;

public class VarianceReplicateCompression extends AReplicateCompression {

    /**
     * Computes the unbiased (sample) variance of a set of replicates.
     *
     * <p>The n-1 denominator matters here: this value feeds the negative-binomial
     * dispersion estimate alpha = (variance - mean) / mean^2. Dividing by n would
     * understate the variance by a third at the three replicates typical of these
     * designs, understating the dispersion and producing anti-conservative Wald
     * p-values.</p>
     */
    protected double calculate(ArrayList<Double> replicates) {
        if (replicates.size() < 2) {
            return 0.0;
        }

        double mean = this.getMean(replicates);
        double variance = 0.0;

        for (Double replicate : replicates) {
            variance += (replicate - mean) * (replicate - mean);
        }

        return variance / (replicates.size() - 1);
    }

    private double getMean(ArrayList<Double> replicates) {
        double mean = 0.0;
        for (Double replicate : replicates) {
            mean += replicate;
        }
        return mean / replicates.size();
    }
}
