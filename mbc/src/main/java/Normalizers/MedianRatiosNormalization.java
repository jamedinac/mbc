package Normalizers;

import Interfaces.IDataNormalizer;

import java.util.Arrays;

public class MedianRatiosNormalization implements IDataNormalizer {
    @Override
    public double[][] normalize(double[][] data) {
        int numberOfGenes = data.length;
        int numberOfSamples = data[0].length;

        double[][] pseudoDaata = new double[numberOfGenes][numberOfSamples];
        for (int i=0; i<numberOfGenes; i++) {
            for (int j=0; j<numberOfSamples; j++) {
                pseudoDaata[i][j] = data[i][j] + 1;
            }
        }

        double[] geometricMean = new double[numberOfGenes];
        for (int i=0; i<numberOfGenes; i++) {
            geometricMean[i] = this.getGeometricMean(pseudoDaata[i]);
        }

        double[][] geometricRatio = new double[numberOfGenes][numberOfSamples];
        for (int i=0; i<numberOfGenes; i++) {
            for (int j=0; j<numberOfSamples; j++) {
                geometricRatio[i][j] = pseudoDaata[i][j] / geometricMean[i];
            }
        }

        double[] sampleMedian = this.getGeometricRatioMedianPerSample(geometricRatio);
        double[][] normalizedData = new double[numberOfGenes][numberOfSamples];

        for (int i=0; i<numberOfGenes; i++) {
            for (int j=0; j<numberOfSamples; j++) {
                normalizedData[i][j] = pseudoDaata[i][j] / sampleMedian[j];
            }
        }

        return normalizedData;
    }

    private double getGeometricMean (double[] data) {
        int n =  data.length;
        double geometricMeanLog = 0.0;
        for (double d : data) {
            geometricMeanLog += Math.log(d);
        }
        return Math.exp(geometricMeanLog / n);
    }

    private double[] getGeometricRatioMedianPerSample(double[][] geometricRatio) {
        int numberOfGenes =  geometricRatio.length;
        int numberOfSamples = geometricRatio[0].length;

        double[] sampleMedian = new  double[numberOfSamples];
        for (int i = 0; i < numberOfSamples; i++) {
            double[] sampleData  = new double[numberOfGenes];

            for (int j=0; j<numberOfGenes; j++) {
                sampleData[j] = geometricRatio[j][i];
            }
            Arrays.sort(sampleData);
            sampleMedian[i] = this.getListMedian(sampleData);
        }
        return sampleMedian;
    }

    private double getListMedian(double[] sampleData) {
        int n = sampleData.length;
        return n%2 == 0 ? (sampleData[(n/2 - 1)] + sampleData[n/2]) / 2.0 : sampleData[n/2];
    }
}
