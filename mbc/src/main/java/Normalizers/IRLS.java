package Normalizers;

import Interfaces.IDataNormalizer;
import Interfaces.IReplicateCompression;
import ReplicateCompression.MeanReplicateCompression;
import ReplicateCompression.VarianceReplicateCompression;
import Utilities.AggregationUtilities;
import Utilities.NormalizationUtilities;

import java.util.ArrayList;

public class IRLS implements IDataNormalizer {

    private final int numberOfTimeSeries;
    private final int numberOfReplicates;
    private final int numberOfSamples;

    private final static double EPS = 1e-9;
    private int numberOfIterations = 100;

    private final MedianRatiosNormalization normalizer = new MedianRatiosNormalization();
    private final IReplicateCompression meanCompression = new MeanReplicateCompression();
    private final IReplicateCompression varianceCompression = new VarianceReplicateCompression();



    public IRLS(int numberOfReplicates, int numberOfTimeSeries) {
        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;
        this.numberOfSamples = numberOfReplicates * numberOfTimeSeries;
    }

    @Override
    public double[][] normalize(double[][] data) {
        double[][] pseudoData = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            pseudoData[i] = NormalizationUtilities.getPseudoData(data[i]);
        }

        double[] scaleFactor = normalizer.getSampleMedian(pseudoData);
        double[][] normalizedData = NormalizationUtilities.getDivideByColumn(pseudoData, scaleFactor);

        double[][] estimatedMean = meanCompression.compress(normalizedData, numberOfReplicates, numberOfTimeSeries);
        double[][] estimatedVariance = varianceCompression.compress(normalizedData, numberOfReplicates, numberOfTimeSeries);
        double[] alphas = this.getEstimatedAlphas(estimatedMean, estimatedVariance);

        double[][] x = this.getDesignMatrix();

        double[][] betas = new double[data.length][numberOfTimeSeries];
        for (int g = 0; g < data.length; g++) {
            betas[g] = this.newtonRaphson(pseudoData[g], alphas[g], scaleFactor, x);
        }
        return betas;
    }

    private double[] getEstimatedAlphas(double[][] estimatedMean, double[][] estimatedVariance) {
        double[] alphas = new  double[estimatedMean.length];

        for (int i=0; i<estimatedMean.length; i++) {
            double avgMean = 0.0;
            double avgVariance = 0.0;

            for (int j=0; j<estimatedMean[i].length; j++) {
                avgMean += estimatedMean[i][j];
                avgVariance += estimatedVariance[i][j];
            }

            avgMean /= estimatedMean[i].length;
            avgVariance /= estimatedVariance[i].length;

            alphas[i] = (avgVariance - avgMean) / (avgMean * avgMean);
            alphas[i] = Math.max(alphas[i], EPS);
        }

        return alphas;
    }

    private double[][] getDesignMatrix() {
        double[][] designMatrix = new double[numberOfSamples][numberOfTimeSeries];

        for (int i=0; i<numberOfSamples; i++) {
            designMatrix[i][0] = designMatrix[i][i/numberOfReplicates] = 1;
        }
        return designMatrix;
    }

    private double[] newtonRaphson(double[] data, double alpha, double[] scaleFactor, double[][] x){
        double[] betas = new double[numberOfTimeSeries];
        double[] variance = new double[numberOfTimeSeries];
        ArrayList<ArrayList<Double>> historicBetas = new ArrayList<>(numberOfTimeSeries);
        for (int i = 0; i < numberOfTimeSeries; i++) {
            historicBetas.add(new ArrayList<>());
        }

        for (int it = 0;  it < numberOfIterations; it++) {
            double[] etas = new double[numberOfSamples];
            for (int i = 0; i < numberOfSamples; ++i) {
                for (int j = 0; j < numberOfTimeSeries; ++j) {
                    etas[i] += x[i][j] * betas[j];
                }
            }

            double[] miu = new double[numberOfSamples];
            for (int i = 0; i < numberOfSamples; ++i) {
                miu[i] += scaleFactor[i] * Math.exp(etas[i]);
            }

            double[] gradient = new double[numberOfTimeSeries];
            for (int i = 0; i < numberOfTimeSeries; ++i) {
                for (int j = 0; j < numberOfSamples; ++j) {
                    gradient[i] += (data[j] - miu[j]) / (1.0 + alpha * miu[j]) * x[j][i];
                }
            }

            double[] weight = new double[numberOfSamples];
            for (int i = 0; i < numberOfSamples; ++i) {
                weight[i] = miu[i] / (1.0 + alpha * miu[i]);
            }

            double[][] h = new double[numberOfTimeSeries][numberOfTimeSeries];
            for (int r = 0; r < numberOfTimeSeries; ++r) {
                for (int c = 0; c < numberOfTimeSeries; ++c) {
                    for (int j = 0; j < numberOfSamples; ++j) {
                        h[r][c] += x[j][r] * weight[j] * x[j][c];
                    }
                }
            }

            if (it > 1) {
                for (int i = 0; i < numberOfTimeSeries; ++i) {
                    variance[i] = AggregationUtilities.variance(historicBetas.get(i));
                }
                for (int i = 1; i < numberOfTimeSeries; ++i) {
                    gradient[i] -= betas[i] / variance[i];
                    h[i][i] += 1.0 / variance[i];
                }
            }

            double[] deltaBetas = this.solveGauss(h, gradient);
            if (deltaBetas == null) {
                break;
            }
            for (int i = 0; i < numberOfTimeSeries; ++i) {
                betas[i] += deltaBetas[i];
                historicBetas.get(i).add(betas[i]);
            }

            if (AggregationUtilities.maxAbsElement(deltaBetas) < EPS) {
                break;
            }
        }

        return betas;
    }

    public double[] solveGauss(double[][] h, double[] gradient) {
        int n = h.length;
        int m = h[0].length;

        double[][] a = new double[n][m + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                a[i][j] = h[i][j];
            }
            a[i][m] = gradient[i];
        }

        double[] ans = new double[m];
        int[] where = new int[m];
        for (int i = 0; i < m; i++) where[i] = -1;

        for (int col = 0, row = 0; col < m && row < n; ++col) {
            int sel = row;
            for (int i = row; i < n; ++i) {
                if (Math.abs(a[i][col]) > Math.abs(a[sel][col])) {
                    sel = i;
                }
            }

            if (Math.abs(a[sel][col]) < EPS) {
                continue;
            }

            double[] tempRow = a[sel];
            a[sel] = a[row];
            a[row] = tempRow;

            where[col] = row;

            for (int i = 0; i < n; ++i) {
                if (i != row) {
                    double c = a[i][col] / a[row][col];
                    for (int j = col; j <= m; ++j) {
                        a[i][j] -= a[row][j] * c;
                    }
                }
            }
            ++row;
        }

        for (int i = 0; i < m; ++i) {
            if (where[i] != -1) {
                ans[i] = a[where[i]][m] / a[where[i]][i];
            }
        }

        for (int i = 0; i < n; ++i) {
            double sum = 0;
            for (int j = 0; j < m; ++j) {
                sum += ans[j] * a[i][j];
            }
            if (Math.abs(sum - a[i][m]) > EPS) {
                return null;
            }
        }

        for (int i = 0; i < m; ++i) {
            if (where[i] == -1) {
                return null;
            }
        }

        return ans;
    }
}
