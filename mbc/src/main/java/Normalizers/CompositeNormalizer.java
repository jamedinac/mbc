package Normalizers;

import Interfaces.IDataNormalizer;

import java.util.ArrayList;

public class CompositeNormalizer implements IDataNormalizer {

    ArrayList<IDataNormalizer> normalizers;

    public CompositeNormalizer() {
        normalizers = new ArrayList<>();
    }

    @Override
    public double[][] normalize(double[][] data) {
        double[][] normalizedData = new double[data.length][data[0].length];

        for(IDataNormalizer normalizer : normalizers){
            normalizedData = normalizer.normalize(data);
        }
        return normalizedData;
    }

    public void add(IDataNormalizer normalizer) {
        normalizers.add(normalizer);
    }
}
