package GeneProfile;

import java.util.ArrayList;
import java.util.Collections;

public class DoubleGeneProfile extends AGeneProfile<Double> {

    public DoubleGeneProfile(ArrayList<Double> profileExpression, String geneId) {
        this.profileExpression = profileExpression;
        this.geneId = geneId;
    }

    @Override
    public ArrayList<Double> getProfileExpression() {
        return profileExpression;
    }

    @Override
    public DoubleGeneProfile add(AGeneProfile<Double> geneProfileB) {
        ArrayList<Double> profileAdditionResult = new ArrayList<>(this.profileExpression.size());

        for (int i=0; i<this.profileExpression.size(); i++) {
            profileAdditionResult.set(i, this.profileExpression.get(i) + geneProfileB.getIndex(i));
        }

        return new DoubleGeneProfile(profileAdditionResult, null);
    }

    @Override
    public Double getMaxExpression() {
        return Collections.max(this.profileExpression);
    }

    /**
     * Computes the Euclidean distance between two gene profiles
     * @param geneProfileB the other gene profile (vector) to get the Euclidean distance
     * @return Euclidean distance
     */
    public Double euclideanDistance (AGeneProfile<Double> geneProfileB) {
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
     * @param m factor
     * @return the same instance of th gene profile
     */
    public DoubleGeneProfile divide(Double m) {
        for (int i=0; i<this.getNumberOfComponents(); i++) {
            this.profileExpression.set(i, this.profileExpression.get(i)/ m);
        }

        return this;
    }
}
