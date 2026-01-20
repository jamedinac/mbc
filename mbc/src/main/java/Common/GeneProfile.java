package Common;

public class GeneProfile{

    private final int numberOfReplicates;
    private final int numberOfTimeSeries;

    public GeneProfile(int numberOfReplicates, int numberOfTimeSeries) {
        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;
    }

    public double computeEuclideanDistance(double[] geneProfileA, double[] geneProfileB) {
        double distance = 0.0;
        int numberOfComponents = this.numberOfTimeSeries *this.numberOfReplicates;

        for (int componentIndex = 0; componentIndex < numberOfComponents; componentIndex++) {
            double difference = geneProfileA[componentIndex] - geneProfileB[componentIndex];
            distance += difference * difference;
        }

        return Math.sqrt(distance);
    }
}
