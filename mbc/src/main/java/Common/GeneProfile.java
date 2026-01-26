package Common;

import java.util.ArrayList;
import java.util.Collections;

public class GeneProfile{

    private final ArrayList<Double> profileData;

    public GeneProfile(ArrayList<Double> profileData){
        this.profileData = profileData;
    }

    public GeneProfile(int size, Double value) {
        this.profileData = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            profileData.set(i, value);
        }
    }

    public ArrayList<Double> getProfileData(){
        return profileData;
    }

    public int getSize(){
        return profileData.size();
    }

    public Double get(int index){
        return profileData.get(index);
    }

    public Double computeEuclideanDistance(GeneProfile geneProfileB) {
        Double distance = 0.0;
        int numberOfComponents = this.getSize();

        for (int componentIndex = 0; componentIndex < numberOfComponents; componentIndex++) {
            double difference = this.get(componentIndex) - geneProfileB.get(componentIndex);
            distance += difference * difference;
        }

        return Math.sqrt(distance);
    }

    public Double getMaxValue(GeneProfile geneProfile) {
        return Collections.max(geneProfile.getProfileData());
    }

    public GeneProfile add(GeneProfile geneProfileB) {
        ArrayList<Double> profileAdditionResult = new ArrayList<>(this.getSize());

        for (int i=0; i<this.getSize(); i++) {
            profileAdditionResult.set(i, this.get(i) + geneProfileB.get(i));
        }

        return new GeneProfile(profileAdditionResult);
    }

    public GeneProfile divide(Double m) {
        ArrayList<Double> profileAdditionResult = new ArrayList<>(this.getSize());

        for (int i=0; i<this.getSize(); i++) {
            profileAdditionResult.set(i, this.get(i) / m);
        }

        return new GeneProfile(profileAdditionResult);
    }
}
