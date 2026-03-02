package ClusteringAlgorithms;

import Common.GeneClusterData;
import Common.GeneExpressionData;
import DataGenerators.RandomGenerator;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IGeneDistance;

public class FuzzyCMeansAlgorithm implements IClusteringAlgorithm {
    private final int k;
    private final double m; // Fuzziness parameter
    private final int maxIterations;
    private final double epsilon;
    private final IGeneDistance geneDistance;

    public FuzzyCMeansAlgorithm(int k, double m, int maxIterations, double epsilon, IGeneDistance geneDistance) {
        this.k = k;
        this.m = m;
        this.maxIterations = maxIterations;
        this.epsilon = epsilon;
        this.geneDistance = geneDistance;
    }

    @Override
    public GeneClusterData clusterGenes(GeneExpressionData data) {
        int n = data.getNumberOfGenes();
        int d = data.getNumberOfComponents();
        double[][] u = initializeMembershipMatrix(n);
        double[][] centroids = new double[k][d];

        for (int iter = 0; iter < maxIterations; iter++) {
            double[][] uOld = copyMatrix(u);
            
            // 1. Calculate Centroids
            for (int j = 0; j < k; j++) {
                centroids[j] = calculateCentroid(j, u, data);
            }

            // 2. Update Membership Matrix
            u = updateMembershipMatrix(u, centroids, data);

            // 3. Convergence Check
            if (calculateMatrixDiff(u, uOld) < epsilon) break;
        }

        return new GeneClusterData(n, k, data.getGeneIds(), u);
    }

    private double[][] initializeMembershipMatrix(int n) {
        double[][] u = new double[n][k];
        for (int i = 0; i < n; i++) {
            double sum = 0;
            for (int j = 0; j < k; j++) {
                u[i][j] = RandomGenerator.uniformRandomInt(100) + 1;
                sum += u[i][j];
            }
            for (int j = 0; j < k; j++) u[i][j] /= sum;
        }
        return u;
    }

    private double[] calculateCentroid(int clusterIdx, double[][] u, GeneExpressionData data) {
        int d = data.getNumberOfComponents();
        double[] numerator = new double[d];
        double denominator = 0;

        for (int i = 0; i < data.getNumberOfGenes(); i++) {
            double weight = Math.pow(u[i][clusterIdx], m);
            double[] profile = data.getGeneProfile(i);
            for (int l = 0; l < d; l++) numerator[l] += weight * profile[l];
            denominator += weight;
        }

        for (int l = 0; l < d; l++) numerator[l] /= denominator;
        return numerator;
    }

    private double[][] updateMembershipMatrix(double[][] u, double[][] centroids, GeneExpressionData data) {
        int n = data.getNumberOfGenes();
        double[][] nextU = new double[n][k];
        double exponent = 2.0 / (m - 1.0);

        for (int i = 0; i < n; i++) {
            double[] x_i = data.getGeneProfile(i);
            for (int j = 0; j < k; j++) {
                double dist_ij = geneDistance.getDistance(x_i, centroids[j]);
                if (dist_ij == 0) { // Handle singularity
                    for (int l = 0; l < k; l++) nextU[i][l] = (l == j) ? 1.0 : 0.0;
                    break;
                }
                double sum = 0;
                for (int l = 0; l < k; l++) {
                    double dist_il = geneDistance.getDistance(x_i, centroids[l]);
                    sum += Math.pow(dist_ij / dist_il, exponent);
                }
                nextU[i][j] = 1.0 / sum;
            }
        }
        return nextU;
    }

    private double calculateMatrixDiff(double[][] a, double[][] b) {
        double maxDiff = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                maxDiff = Math.max(maxDiff, Math.abs(a[i][j] - b[i][j]));
            }
        }
        return maxDiff;
    }

    private double[][] copyMatrix(double[][] m) {
        double[][] copy = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++) System.arraycopy(m[i], 0, copy[i], 0, m[i].length);
        return copy;
    }
}
