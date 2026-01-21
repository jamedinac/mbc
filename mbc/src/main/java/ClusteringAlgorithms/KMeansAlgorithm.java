package ClusteringAlgorithms;

import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import Common.GeneProfile;
import DataGenerators.UniformDataGenerator;
import Interfaces.IClusteringAlgorithm;
import Utilities.Utilities;

import java.util.ArrayList;

public class KMeansAlgorithm implements IClusteringAlgorithm {

    int k;
    int maxIterations;

    public KMeansAlgorithm(int k, int maxIterations) {
        this.k = k;
        this.maxIterations = maxIterations;
    }

    @Override
    public GeneClusteringResult clusterGenes(GeneExpressionData geneExpresionData) {
        int numberOfRows = geneExpresionData.getNumberOfGenes();
        int numberOfColumns = geneExpresionData.getNumberOfTimeSeries() * geneExpresionData.getNumberOfReplicates();

        ArrayList<GeneProfile<Double>> centroids = generateCentroids(
                getMaxExpressionValue(geneExpresionData.getExpressionData()),
                numberOfRows,
                numberOfColumns);

        ArrayList<GeneProfile<Double>> clusteringResult = new ArrayList<>();

        for  (int iteration = 0; iteration < maxIterations; iteration++) {
            clusteringResult = getClusterAssignation(geneExpresionData, centroids);
            centroids = calculateCentroids(clusteringResult, geneExpresionData);
        }

        return new GeneClusteringResult(k, clusteringResult, geneExpresionData);
    }

    ArrayList<GeneProfile<Double>> generateCentroids(double maxExpressionValue, int numberOfRows, int numberOfColumns) {
        ArrayList<GeneProfile<Double>> centroids = new ArrayList<>();
        UniformDataGenerator dataGenerator = new UniformDataGenerator((int)maxExpressionValue);

        for (int i=0; i<numberOfRows; i++) {
            ArrayList<Double> centroid = new ArrayList<>(numberOfColumns);

            for (int j=0; j<numberOfColumns; j++) {
                centroid.set(j, dataGenerator.generateRandomDouble());
            }

            centroids.add(new GeneProfile<>(centroid));
        }

        return centroids;
    }
    
    double getMaxExpressionValue(ArrayList<GeneProfile<Double>> expressionData) {
        double maxExpression = 0.0;

        for (GeneProfile<Double> geneProfile : expressionData) {
            maxExpression = Utilities.getMaxValue(geneProfile);
        }

        return maxExpression;
    }

    ArrayList<GeneProfile<Double>> getClusterAssignation(GeneExpressionData geneExpressionData, ArrayList<GeneProfile<Double>> centroids) {
        int numberOfGenes = geneExpressionData.getExpressionData().size();
        int numberOfClusters = centroids.size();

        ArrayList<GeneProfile<Double>> clusterResult = new ArrayList<>(numberOfGenes);

        for (int gene = 0; gene < numberOfGenes; gene++) {
            int bestCentroid = 0;
            double distanceToBestCentorid = Utilities.computeEuclideanDistance(geneExpressionData.getGeneProfile(gene), centroids.getFirst());
            
            for (int centroid = 1; centroid<numberOfClusters; centroid++) {
                double distanceToCurrentCentroid = Utilities.computeEuclideanDistance(geneExpressionData.getGeneProfile(gene), centroids.getFirst());

                if (distanceToCurrentCentroid < distanceToBestCentorid) {
                    bestCentroid = centroid;
                    distanceToBestCentorid = distanceToCurrentCentroid;
                }
            }

            ArrayList<Double> clusterAssignments = new ArrayList<>(numberOfClusters);

            for (int centroid = 0; centroid<numberOfClusters; centroid++) {
                clusterAssignments.set(centroid, centroid == bestCentroid ? 1.0 : 0.0);
            }

            clusterResult.add(new GeneProfile<>(clusterAssignments));
        }

        return clusterResult;
    }

    ArrayList<GeneProfile<Double>> calculateCentroids(ArrayList<GeneProfile<Double>> clusteringResult, GeneExpressionData geneExpressionData) {
        ArrayList<GeneProfile<Double>> centroids = new ArrayList<>();

        for (int centroid = 0; centroid < this.k; centroid++) {
            int numberOfElements = 0;
            GeneProfile<Double> centroidProfile = new GeneProfile<>(this.k, 0.0);

            for (GeneProfile<Double> geneProfile : clusteringResult) {
                if (geneProfile.get(centroid) != 0.0) {
                    centroidProfile = Utilities.add(centroidProfile, geneProfile);
                    numberOfElements++;
                }
            }

            centroids.add(Utilities.divide(centroidProfile, (double) numberOfElements));
        }

        return centroids;
    }
}
