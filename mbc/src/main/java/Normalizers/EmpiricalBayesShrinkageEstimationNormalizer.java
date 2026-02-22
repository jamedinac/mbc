package Normalizers;

import Interfaces.IDataNormalizer;

public class EmpiricalBayesShrinkageEstimationNormalizer implements IDataNormalizer {

    private int numberOfTimeSeries;
    private int numberOfReplicates;
    private double epsilon;

    private CountDistributionNormalizer countDistributionNormalizer;
    private IDataNormalizer medianRatioNormalizer = new MedianRatiosNormalization();


    public EmpiricalBayesShrinkageEstimationNormalizer(int numberOfReplicates, int numberOfTimeSeries, double epsilon) {
        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;
        this.epsilon = epsilon;

        countDistributionNormalizer = new CountDistributionNormalizer(numberOfReplicates, numberOfTimeSeries);
    }

    @Override
    public double[][] normalize(double[][] data) {
        data =  medianRatioNormalizer.normalize(data);

        double[][] estimatedMean = countDistributionNormalizer.getEstimatedMean(data);
        double[][] estimatedVariance = this.getEstimatedVariance(data, estimatedMean);
        double[][] estimatedDispersion = this.getEstimatedDispersion(estimatedMean, estimatedVariance);

        double[] alphaMLE = this.getRowMean(estimatedDispersion);
        double[] globalMean = this.getRowMean(estimatedMean);

        double globalX = this.getLogMean(globalMean);
        double globalY = this.getLogMean(alphaMLE);

        double slope = this.getSlope(globalX, globalY, globalMean, alphaMLE);
        double cut = globalY - globalX*slope;

        double[] priorDispersion = this.getPriorDispersion(slope, cut, globalMean);
        double[] residue = this.getResidue(priorDispersion, alphaMLE);

        double samplingVariance = 1.0 / (numberOfTimeSeries * (numberOfReplicates - 1));
        double priorVariance = Math.max(this.getVariance(residue) - samplingVariance, epsilon);

        double[] logSamplingVariance = this.getLogSamplingVariance(estimatedDispersion);

        double priorWeight = 1 / priorVariance;
        double[] weightGene = this.getInverseRow(logSamplingVariance);

        double[] alphaShrunk = this.getAlphaShrunk(alphaMLE, priorDispersion, weightGene, priorWeight);

        return this.getProbabilityVector(estimatedMean, alphaShrunk);
    }

    private double[][] getEstimatedVariance(double[][] data, double[][] estimatedMean) {
        double[][] estimatedVariance = new double[data.length][numberOfTimeSeries];

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < numberOfTimeSeries; j++) {
                estimatedVariance[i][j] = 0;

                for (int c = 0; c<numberOfReplicates; c++) {
                    double sum = (data[i][j*numberOfReplicates + c] - estimatedMean[i][j]);
                    estimatedVariance[i][j] += sum*sum;
                }

                estimatedVariance[i][j] /= numberOfReplicates - 1;
            }
        }

        return estimatedVariance;
    }

    private double[][] getEstimatedDispersion(double[][] estimatedMean, double[][] estimatedVariance) {
        double[][] estimatedDispersion = new double[estimatedMean.length][numberOfTimeSeries];

        for (int i = 0; i < estimatedMean.length; i++) {
            for (int j = 0; j < estimatedMean[i].length; j++) {
                estimatedDispersion[i][j] = (estimatedVariance[i][j] - estimatedMean[i][j]) / estimatedMean[i][j]*estimatedMean[i][j];
                estimatedDispersion[i][j] = Math.max(estimatedDispersion[i][j], epsilon);
            }
        }

        return estimatedDispersion;
    }

    private double[] getRowMean(double[][] data) {
        double [] rowMean = new double[data.length];

        for (int i = 0; i < data.length; i++) {
            rowMean[i] = this.getMean(data[i]);
        }

        return  rowMean;
    }

    private double getSlope(double globalX, double globalY, double[] globalMean, double[] alphaMLE) {
        double numerator = 0.0, denominator = 0.0;

        for (int i = 0; i < globalMean.length; i++) {
            numerator += (Math.log(globalMean[i]) - globalX) * (Math.log(alphaMLE[i]) - globalY);
            denominator += (Math.log(globalMean[i]) - globalX) * (Math.log(globalMean[i]) - globalX);
        }

        return numerator / denominator;
    }

    private double[] getPriorDispersion(double slope, double cut, double[] data) {
        double[] priorDispersion =  new double[data.length];

        for (int i = 0; i < data.length; i++) {
            priorDispersion[i] = data[i]*slope + cut;
        }

        return priorDispersion;
    }

    private double[] getResidue(double[] priorDispersion, double[] alphaMLE) {
        double[] residue = new double[priorDispersion.length];

        for (int i = 0; i < priorDispersion.length; i++) {
            residue[i] = Math.log(alphaMLE[i]) - priorDispersion[i];
        }

        return residue;
    }

    private double[] getLogSamplingVariance(double[][] alpha) {
        double[] logSamplingVariance = new double[alpha.length];

        for (int i = 0; i < alpha.length; i++) {
            double logMean = this.getLogMean(alpha[i]);
            double samplingVariance = 0.0;

            for  (int j = 0; j < alpha[i].length; j++) {
                samplingVariance += (Math.log(alpha[i][j]) -  logMean) * (Math.log(alpha[i][j]) - logMean);
            }

            logSamplingVariance[i] = samplingVariance / alpha[i].length;
        }

        return logSamplingVariance;
    }

    private double[] getInverseRow(double[] logSamplingVariance) {
        double[] weightGene = new double[logSamplingVariance.length];

        for (int i = 0; i < logSamplingVariance.length; i++) {
            weightGene[i] = 1 / logSamplingVariance[i];
        }

        return weightGene;
    }

    private double getVariance(double[] residue) {
        double mean = this.getMean(residue);
        double variance = 0.0;

        for (int i = 0; i < residue.length; i++) {
            variance += (residue[i] - mean) * (residue[i] - mean);
        }

        return variance / (residue.length - 1);
    }

    private double getLogMean(double[] data) {
        double logMean = 0.0;

        for (int i = 0; i < data.length; i++) {
            logMean += Math.log(data[i]);
        }

        return logMean /  data.length;
    }

    private double getMean(double[] data) {
        double mean = 0.0;
        for (int i = 0; i < data.length; i++) {
            mean += data[i];
        }
        return mean /  data.length;
    }

    private double[] getAlphaShrunk(double[] alphaMLE, double[] priorDispersion, double[] weightGene, double priorWeight) {
        double[] alphaShrunk = new double[priorDispersion.length];

        for (int i = 0; i < priorDispersion.length; i++) {
            alphaShrunk[i] = weightGene[i]*Math.log(alphaMLE[i]) +  priorWeight*priorDispersion[i];
            alphaShrunk[i] /= weightGene[i] + priorWeight;
            alphaShrunk[i] = Math.exp(alphaShrunk[i]);
        }

        return alphaShrunk;
    }

    private double[][] getProbabilityVector(double[][] estimatedMean, double[] alphaShrunk) {
        double[][] probabilityVector = new double[estimatedMean.length][estimatedMean[0].length];

        for (int i = 0; i < estimatedMean.length; i++) {
            double sum = 0.0;

            for (int j = 0; j < estimatedMean[i].length; j++) {
                sum += estimatedMean[i][j] / Math.sqrt(estimatedMean[i][j] + alphaShrunk[i]*estimatedMean[i][j]*estimatedMean[i][j]);
            }

            for (int j = 0; j < estimatedMean[i].length; j++) {
                probabilityVector[i][j] = estimatedMean[i][j] / Math.sqrt(estimatedMean[i][j] + alphaShrunk[i]*estimatedMean[i][j]*estimatedMean[i][j]) / sum;
            }
        }

        return probabilityVector;
    }
}
