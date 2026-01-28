package GeneProfile;

import Interfaces.IGeneProfile;

import java.util.ArrayList;
import java.util.Collections;

public class GeneProfileVector implements IGeneProfile<Double> {

    private ArrayList<Double> profileExpression;

    public GeneProfileVector(ArrayList<Double> profileExpression) {
        this.profileExpression = profileExpression;
    }

    @Override
    public ArrayList<Double> getProfileExpression() {
        return profileExpression;
    }

    @Override
    public IGeneProfile add(IGeneProfile geneProfileB) {
        ArrayList<Double> profileAdditionResult = new ArrayList<>(this.profileExpression.size());

        for (int i=0; i<this.profileExpression.size(); i++) {
            profileAdditionResult.set(i, this.profileExpression.get(i) + geneProfileB.getIndex(i).doubleValue());
        }

        return new GeneProfileVector(profileAdditionResult);
    }

    @Override
    public Double getMaxExpression() {
        return Collections.max(this.profileExpression);
    }

    @Override
    public Double getIndex(int i) {
        return  this.profileExpression.get(i);
    }

    @Override
    public Integer getNumberOfComponents() {
        return this.profileExpression.size();
    }

    /**
     * Computes the eucledean distance between two gene profiles
     * @param geneProfileB the other gene profile (vector) to get the Euclidean distance
     * @return Euclidean distance
     */
    public Double euclideanDistance (GeneProfileVector geneProfileB) {
        double distance = 0.0;
        int numberOfComponents = this.getNumberOfComponents();

        for (int componentIndex = 0; componentIndex < numberOfComponents; componentIndex++) {
            double difference = this.getIndex(componentIndex) - geneProfileB.getIndex(componentIndex);
            distance += difference * difference;
        }

        return Math.sqrt(distance);
    }

    /**
     * Divides all the component values by the given factor M
     * @param m
     * @return the same instance of th gene profile
     */
    public GeneProfileVector divide(Double m) {
        for (int i=0; i<this.getNumberOfComponents(); i++) {
            this.profileExpression.set(i, this.profileExpression.get(i)/ m);
        }

        return this;
    }
}
