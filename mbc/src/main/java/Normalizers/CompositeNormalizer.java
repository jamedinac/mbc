package Normalizers;

import Interfaces.IDataNormalizer;

import java.util.ArrayList;
import java.util.List;

public class CompositeNormalizer implements IDataNormalizer {

    private final List<IDataNormalizer> normalizers;

    public CompositeNormalizer() {
        this.normalizers = new ArrayList<>();
    }

    public void add(IDataNormalizer normalizer) {
        this.normalizers.add(normalizer);
    }

    @Override
    public double[][] normalize(double[][] data, int[] replicatesPerTime, int[] sampleTimeMap, int numberOfTimeSeries) {
        double[][] result = data;

        for (IDataNormalizer normalizer : this.normalizers) {
            result = normalizer.normalize(result, replicatesPerTime, sampleTimeMap, numberOfTimeSeries);
        }

        return result;
    }
}
