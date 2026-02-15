package GeneDistance;

import Interfaces.IGeneDistance;

public class JensenShannonDistance implements IGeneDistance {
    @Override
    public double getDistance(double[] geneExpressionData, double[] centroid) {
        double[] mean = this.getMean(geneExpressionData, centroid);
        double jsd = this.getKullbackLeiblerDivergence(geneExpressionData, mean) + this.getKullbackLeiblerDivergence(centroid, mean);
        return Math.sqrt(jsd / 2.0);
    }

    private double getKullbackLeiblerDivergence(double[] p, double[] q) {
        double result = 0;
        for (int i = 0; i < p.length; i++) {
            result += p[i] * Math.log(p[i]/q[i]);
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
