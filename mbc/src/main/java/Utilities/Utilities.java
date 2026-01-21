package Utilities;

import Common.GeneProfile;

import java.util.ArrayList;
import java.util.Collections;

public class Utilities {
    public static <T extends Number> double computeEuclideanDistance(GeneProfile<T> geneProfileA, GeneProfile<T> geneProfileB) {
        double distance = 0.0;
        int numberOfComponents = geneProfileA.getSize();

        for (int componentIndex = 0; componentIndex < numberOfComponents; componentIndex++) {
            double difference = geneProfileA.get(componentIndex).doubleValue() - geneProfileB.get(componentIndex).doubleValue();
            distance += difference * difference;
        }

        return Math.sqrt(distance);
    }

    public static <T extends Comparable<? super T>> T getMaxValue(GeneProfile<T> geneProfile) {
        return Collections.max(geneProfile.getProfileData());
    }

    public static GeneProfile<Double> add(GeneProfile<Double> geneProfileA, GeneProfile<Double> geneProfileB) {
        ArrayList<Double> profileAdditionResult = new ArrayList<>(geneProfileA.getSize());

        for (int i=0; i<geneProfileA.getSize(); i++) {
            profileAdditionResult.set(i, geneProfileA.get(i).doubleValue() + geneProfileB.get(i).doubleValue());
        }

        return new GeneProfile<>(profileAdditionResult);
    }

    public static GeneProfile<Double> divide(GeneProfile<Double> geneProfile, Double m) {
        ArrayList<Double> profileAdditionResult = new ArrayList<>(geneProfile.getSize());

        for (int i=0; i<geneProfile.getSize(); i++) {
            profileAdditionResult.set(i, geneProfile.get(i).doubleValue() / m);
        }

        return new GeneProfile<>(profileAdditionResult);
    }
}
