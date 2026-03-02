package Normalizers;

import Interfaces.IDataNormalizer;
import ReplicateCompression.ReplicateCompressionFactory;
import Utilities.NormalizationUtilities;

public class MedianRatiosNormalization implements IDataNormalizer {
    @Override
    public double[][] normalize(double[][] data) {
        int n = data.length;
        int m = data[0].length;

        double[][] pseudoData = this.getPseudoData(data);
        double[] sampleMedian = this.getSampleMedian(pseudoData);
        return NormalizationUtilities.getDivideByColumn(pseudoData, sampleMedian);
    }

    public double[] getSampleMedian(double[][] data) {
        int n = data.length;

        double[] geometricMean = this.getGeometricMeanPerRow(data);

        double[][] geometricRatio = NormalizationUtilities.getDivideByRow(data, geometricMean);
        return NormalizationUtilities.getColumnMedian(geometricRatio);
    }

    private double[][] getPseudoData(double[][] data) {
        int n = data.length;
        int m = data[0].length;
        double[][] pseudoData = new double[n][m];

        for (int i=0; i<n; i++) {
            pseudoData[i] = NormalizationUtilities.getPseudoData(data[i]);
        }

        return pseudoData;
    }

    private double[] getGeometricMeanPerRow(double[][] data) {
        int n = data.length;

        double[]geometricMean = new double[n];
        for (int i=0; i<n; i++) {
            geometricMean[i] = NormalizationUtilities.getGeometricMean(data[i]);
        }

        return  geometricMean;
    }
}
