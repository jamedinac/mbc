package Normalizers;

import Interfaces.IDataNormalizer;
import Utilities.NormalizationUtilities;

import java.util.ArrayList;
import java.util.List;

public class MedianRatiosNormalization implements IDataNormalizer {

    @Override
    public double[][] normalize(double[][] data, int[] replicatesPerTime, int[] sampleTimeMap, int numberOfTimeSeries) {
        double[] sampleMedian = this.getSampleMedian(data);
        return NormalizationUtilities.getDivideByColumn(data, sampleMedian);
    }

    /**
     * Estimates the size factor of every sample: the median across genes of the ratio
     * between a gene's count and its geometric mean over all samples.
     *
     * <p>Only genes that are strictly positive in every sample take part in the estimation.
     * A single zero count sends that gene's geometric mean to zero and its ratios to
     * infinity, which would otherwise poison the median of every column and collapse the
     * whole normalized matrix to NaN.</p>
     *
     * @param data the raw count matrix [genes][samples]
     * @return one size factor per sample
     */
    public double[] getSampleMedian(double[][] data) {
        double[][] reference = this.getReferenceGenes(data);

        double[] geometricMean = new double[reference.length];
        for (int i = 0; i < reference.length; i++) {
            geometricMean[i] = NormalizationUtilities.getGeometricMean(reference[i]);
        }

        double[][] ratios = NormalizationUtilities.getDivideByRow(reference, geometricMean);
        double[] sizeFactors = NormalizationUtilities.getColumnMedian(ratios);

        for (int j = 0; j < sizeFactors.length; j++) {
            if (!Double.isFinite(sizeFactors[j]) || sizeFactors[j] <= 0) {
                throw new IllegalStateException(
                        "Unable to estimate a valid size factor for sample index " + j + " (got "
                                + sizeFactors[j] + "). The count matrix is degenerate: too many zero "
                                + "or negative counts to establish a reference. Consider filtering with "
                                + "--filter non-zero or total-expression.");
            }
        }

        return sizeFactors;
    }

    /**
     * Selects the genes used as the reference for size factor estimation: those with a
     * strictly positive count in every sample. Falls back to the pseudo-counted matrix
     * when no gene qualifies, so that a size factor can still be estimated on very
     * sparse data instead of failing outright.
     *
     * @param data the raw count matrix [genes][samples]
     * @return the subset of rows to estimate from, or the pseudo-counted matrix
     */
    private double[][] getReferenceGenes(double[][] data) {
        List<double[]> reference = new ArrayList<>();

        for (double[] geneRow : data) {
            if (this.isStrictlyPositive(geneRow)) {
                reference.add(geneRow);
            }
        }

        if (reference.isEmpty()) {
            double[][] pseudoData = new double[data.length][];
            for (int i = 0; i < data.length; i++) {
                pseudoData[i] = NormalizationUtilities.getPseudoData(data[i]);
            }
            return pseudoData;
        }

        return reference.toArray(new double[0][]);
    }

    private boolean isStrictlyPositive(double[] geneRow) {
        for (double count : geneRow) {
            if (!(count > 0)) {
                return false;
            }
        }
        return true;
    }
}
