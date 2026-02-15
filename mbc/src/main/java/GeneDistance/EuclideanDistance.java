package GeneDistance;

import Interfaces.IGeneDistance;

public class EuclideanDistance implements IGeneDistance {
    @Override
    public double getDistance(double[] geneExpressionData, double[] centroid) {
        double distance = 0.0;
        int numberOfComponents = geneExpressionData.length;

        for (int componentIndex = 0; componentIndex < numberOfComponents; componentIndex++) {
            double difference = geneExpressionData[componentIndex] - centroid[componentIndex];
            distance += difference * difference;
        }

        return Math.sqrt(distance);
    }
}
