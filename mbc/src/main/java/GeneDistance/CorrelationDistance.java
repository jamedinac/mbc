package GeneDistance;

import Interfaces.IGeneDistance;

public class CorrelationDistance implements IGeneDistance {
    @Override
    public double getDistance(double[]  geneA, double[] geneB) {
        double meanA = this.getMeanFromGeneProfile(geneA);
        double meanB = this.getMeanFromGeneProfile(geneB);

        double covariance = 0.0;
        for (int i=0; i<geneA.length; i++){
            covariance += (geneA[i] -  meanA) * (geneB[i] - meanB);
        }
        covariance /= geneA.length - 1;

        double sdA = this.getSDFromGeneProfile(geneA);
        double sdB = this.getSDFromGeneProfile(geneB);

        return 1.0 - covariance / (sdA * sdB);
    }

    private double getSDFromGeneProfile(double[] gene) {
        double mean = this.getMeanFromGeneProfile(gene);
        double sd = 0.0;
        for (int i=0; i<gene.length; i++){
            sd += (gene[i] - mean) * (gene[i] - mean);
        }

        return Math.sqrt(sd / (gene.length - 1));
    }

    private double getMeanFromGeneProfile(double[] gene) {
        double mean = 0.0;
        for (int i=0;i<gene.length;i++) {
            mean += gene[i];
        }
        return mean / (double)gene.length;
    }
}

