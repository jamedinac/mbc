package ModelFitters;

import Common.GLMFitResult;
import Interfaces.IModelFitter;
import Interfaces.IReplicateCompression;
import ReplicateCompression.MeanReplicateCompression;
import ReplicateCompression.VarianceReplicateCompression;
import Utilities.AggregationUtilities;

public class GLMProcessor implements IModelFitter {

    private static final double CONVERGENCE_TOL = 1e-7;
    private static final double PIVOT_TOL = 1e-12;
    private static final double ALPHA_FLOOR = 1e-9;
    private static final double SOLVE_TOL = 1e-6;

    private final IReplicateCompression meanCompression = new MeanReplicateCompression();
    private final IReplicateCompression varianceCompression = new VarianceReplicateCompression();

    public GLMProcessor() {}

    @Override
    public GLMFitResult fitModel(double[][] normalizedData, int[] replicatesPerTime, int[] sampleTimeMap, int numberOfTimeSeries) {
        int numberOfSamples = normalizedData[0].length;
        int nGenes = normalizedData.length;

        double[][] estimatedMean = meanCompression.compress(normalizedData, replicatesPerTime, numberOfTimeSeries);
        double[][] estimatedVariance = varianceCompression.compress(normalizedData, replicatesPerTime, numberOfTimeSeries);
        double[] alphas = this.getEstimatedAlphas(estimatedMean, estimatedVariance);

        double[][] designMatrix = this.getDesignMatrix(sampleTimeMap, numberOfSamples, numberOfTimeSeries);

        double[] scaleFactor = new double[numberOfSamples];
        for (int i = 0; i < numberOfSamples; i++) {
            scaleFactor[i] = 1.0;
        }

        double[][] mleBetas = new double[nGenes][numberOfTimeSeries];
        for (int g = 0; g < nGenes; g++) {
            IRLSResult result = this.iterativelyReweightedLeastSquares(normalizedData[g], alphas[g], scaleFactor, designMatrix, null, numberOfSamples, numberOfTimeSeries);
            mleBetas[g] = result.betas;
        }

        double[] priorVariance = this.estimatePriorVariance(mleBetas, numberOfTimeSeries);

        double[][] betas = new double[nGenes][numberOfTimeSeries];
        double[][] weights = new double[nGenes][numberOfSamples];

        for (int g = 0; g < nGenes; g++) {
            IRLSResult result = this.iterativelyReweightedLeastSquares(normalizedData[g], alphas[g], scaleFactor, designMatrix, priorVariance, numberOfSamples, numberOfTimeSeries);
            betas[g] = result.betas;
            weights[g] = result.weights;
        }

        return new GLMFitResult(betas, weights, priorVariance, designMatrix, alphas);
    }

    private double[] estimatePriorVariance(double[][] mleBetas, int numberOfTimeSeries) {
        int nGenes = mleBetas.length;
        double[] priorVariance = new double[numberOfTimeSeries];

        for (int p = 1; p < numberOfTimeSeries; p++) {
            double sum = 0.0;
            double sumSq = 0.0;
            for (int g = 0; g < nGenes; g++) {
                sum += mleBetas[g][p];
                sumSq += mleBetas[g][p] * mleBetas[g][p];
            }
            double mean = sum / nGenes;
            priorVariance[p] = (sumSq / nGenes) - (mean * mean);
            priorVariance[p] = Math.max(priorVariance[p], ALPHA_FLOOR);
        }
        return priorVariance;
    }

    private double[] getEstimatedAlphas(double[][] estimatedMean, double[][] estimatedVariance) {
        double[] alphas = new double[estimatedMean.length];
        for (int i = 0; i < estimatedMean.length; i++) {
            double avgMean = 0.0;
            double avgVariance = 0.0;

            for (int j = 0; j < estimatedMean[i].length; j++) {
                avgMean += estimatedMean[i][j];
                avgVariance += estimatedVariance[i][j];
            }
            avgMean /= estimatedMean[i].length;
            avgVariance /= estimatedVariance[i].length;

            alphas[i] = (avgVariance - avgMean) / (avgMean * avgMean);
            alphas[i] = Math.max(alphas[i], ALPHA_FLOOR);
        }
        return alphas;
    }

    private double[][] getDesignMatrix(int[] sampleTimeMap, int numberOfSamples, int numberOfTimeSeries) {
        double[][] designMatrix = new double[numberOfSamples][numberOfTimeSeries];
        for (int i = 0; i < numberOfSamples; i++) {
            designMatrix[i][0] = 1; 
            designMatrix[i][sampleTimeMap[i]] = 1;
        }
        return designMatrix;
    }

    private IRLSResult iterativelyReweightedLeastSquares(double[] data, double alpha, double[] scaleFactor, double[][] x, double[] priorVariance, int numberOfSamples, int numberOfTimeSeries) {
        double[] betas = new double[numberOfTimeSeries];
        double meanData = 0;
        double meanScale = 0;
        for (int i = 0; i < numberOfSamples; i++) {
            meanData += data[i];
            meanScale += scaleFactor[i];
        }
        betas[0] = Math.log((meanData / numberOfSamples) / (meanScale / numberOfSamples));

        double[] logScaleFactor = new double[numberOfSamples];
        for (int i = 0; i < numberOfSamples; i++) {
            logScaleFactor[i] = Math.log(scaleFactor[i]);
        }

        double[] weight = new double[numberOfSamples];

        int numberOfIterations = 100;
        for (int it = 0; it < numberOfIterations; it++) {
            double[] logMu = new double[numberOfSamples];
            for (int i = 0; i < numberOfSamples; ++i) {
                for (int j = 0; j < numberOfTimeSeries; ++j) {
                    logMu[i] += x[i][j] * betas[j];
                }
                logMu[i] += logScaleFactor[i];
            }

            double[] gradient = new double[numberOfTimeSeries];
            for (int i = 0; i < numberOfTimeSeries; ++i) {
                for (int j = 0; j < numberOfSamples; ++j) {
                    double g;
                    if (logMu[j] >= 0) {
                        double expNeg = Math.exp(-logMu[j]);
                        g = (data[j] * expNeg - 1.0) / (expNeg + alpha);
                    } else {
                        double expPos = Math.exp(logMu[j]);
                        g = (data[j] - expPos) / (1.0 + alpha * expPos);
                    }
                    gradient[i] += g * x[j][i];
                }
            }

            for (int i = 0; i < numberOfSamples; ++i) {
                if (logMu[i] >= 0) {
                    double expNeg = Math.exp(-logMu[i]);
                    weight[i] = 1.0 / (expNeg + alpha);
                } else {
                    double expPos = Math.exp(logMu[i]);
                    weight[i] = expPos / (1.0 + alpha * expPos);
                }
            }

            double[][] h = new double[numberOfTimeSeries][numberOfTimeSeries];
            for (int r = 0; r < numberOfTimeSeries; ++r) {
                for (int c = 0; c < numberOfTimeSeries; ++c) {
                    for (int j = 0; j < numberOfSamples; ++j) {
                        h[r][c] += x[j][r] * weight[j] * x[j][c];
                    }
                }
            }

            if (priorVariance != null) {
                for (int i = 1; i < numberOfTimeSeries; ++i) {
                    gradient[i] -= betas[i] / priorVariance[i];
                    h[i][i] += 1.0 / priorVariance[i];
                }
            }

            double[] deltaBetas = this.solveGauss(h, gradient);
            if (deltaBetas == null) break;
            
            for (int i = 0; i < numberOfTimeSeries; ++i) {
                betas[i] += deltaBetas[i];
            }

            if (AggregationUtilities.maxAbsElement(deltaBetas) < CONVERGENCE_TOL) break;
        }

        return new IRLSResult(betas, weight);
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
                if (Math.abs(a[i][col]) > Math.abs(a[sel][col])) sel = i;
            }
            if (Math.abs(a[sel][col]) < PIVOT_TOL) continue;

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
            if (where[i] != -1) ans[i] = a[where[i]][m] / a[where[i]][i];
        }

        for (int i = 0; i < n; ++i) {
            double sum = 0;
            for (int j = 0; j < m; ++j) sum += ans[j] * a[i][j];
            if (Math.abs(sum - a[i][m]) > SOLVE_TOL) return null;
        }

        for (int i = 0; i < m; ++i) {
            if (where[i] == -1) return null;
        }
        return ans;
    }

    private static class IRLSResult {
        public final double[] betas;
        public final double[] weights;
        public IRLSResult(double[] betas, double[] weights) {
            this.betas = betas;
            this.weights = weights;
        }
    }
}