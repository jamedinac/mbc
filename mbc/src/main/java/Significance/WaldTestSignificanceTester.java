package Significance;

import Common.GLMFitResult;
import Interfaces.ILinearAlgebra;
import Interfaces.IProbabilityProvider;
import Interfaces.ISignificanceTester;

public class WaldTestSignificanceTester implements ISignificanceTester {

    private final ILinearAlgebra math;
    private final IProbabilityProvider probability;

    public WaldTestSignificanceTester(ILinearAlgebra math, IProbabilityProvider probability) {
        this.math = math;
        this.probability = probability;
    }

    @Override
    public double[][] calculateRawPValues(GLMFitResult glmFit) {
        int nGenes = glmFit.betas().length;
        int nTimePoints = glmFit.betas()[0].length;
        int nSamples = glmFit.designMatrix().length;

        double[][] rawPValues = new double[nGenes][nTimePoints];
        double[][] X = glmFit.designMatrix();
        double[] priorVariance = glmFit.priorVariance();

        for (int i = 0; i < nGenes; i++) {
            double[] W = glmFit.weights()[i];
            double[] betas = glmFit.betas()[i];

            double[][] xtwx = new double[nTimePoints][nTimePoints];
            for (int r = 0; r < nTimePoints; r++) {
                for (int c = 0; c < nTimePoints; c++) {
                    for (int j = 0; j < nSamples; j++) {
                        xtwx[r][c] += X[j][r] * W[j] * X[j][c];
                    }
                }
            }

            double[][] lambdaI = new double[nTimePoints][nTimePoints];
            if (priorVariance != null) {
                for (int p = 1; p < nTimePoints; p++) {
                    if (priorVariance[p] > 0) {
                        lambdaI[p][p] = 1.0 / priorVariance[p];
                    }
                }
            }

            double[][] toInvert = math.add(xtwx, lambdaI);

            double[][] inverse;
            try {
                inverse = math.invert(toInvert);
            } catch (Exception e) {
                for (int p = 0; p < nTimePoints; p++) {
                    rawPValues[i][p] = 1.0;
                }
                continue;
            }

            double[][] sigma_i = math.multiply(math.multiply(inverse, xtwx), inverse);
            double[] variances = math.getDiagonal(sigma_i);

            for (int p = 0; p < nTimePoints; p++) {
                if (variances[p] <= 0) {
                    rawPValues[i][p] = 1.0;
                    continue;
                }
                
                double se = Math.sqrt(variances[p]);
                double zScore = betas[p] / se;
                rawPValues[i][p] = probability.calculateTwoTailedPValue(zScore);
            }
        }

        return rawPValues;
    }
}