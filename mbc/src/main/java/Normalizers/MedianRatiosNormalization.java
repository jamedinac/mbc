package Normalizers;

import Interfaces.IDataNormalizer;
import Utilities.NormalizationUtilities;

public class MedianRatiosNormalization implements IDataNormalizer {

    @Override
    public double[][] normalize(double[][] data, int[] replicatesPerTime, int[] sampleTimeMap, int numberOfTimeSeries) {
        double[] sampleMedian = this.getSampleMedian(data);
        return NormalizationUtilities.getDivideByColumn(data, sampleMedian);
    }

    public double[] getSampleMedian(double[][] data) {
        double[] geometricMean = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            geometricMean[i] = NormalizationUtilities.getGeometricMean(data[i]);
        }

        double[][] ratios = NormalizationUtilities.getDivideByRow(data, geometricMean);
        return NormalizationUtilities.getColumnMedian(ratios);
    }
}
