package ClusteringAlgorithms;

import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import Common.GeneProfile;
import DataGenerators.UniformDataGenerator;
import Interfaces.IClusteringAlgorithm;

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

        double[][] centroids = generateCentroids(
                getMaxExpressionValue(geneExpresionData.getExpressionData()),
                numberOfRows,
                numberOfColumns);

        double[][] clusteringResult = new double[numberOfRows][numberOfColumns];

        for  (int iteration = 0; iteration < maxIterations; iteration++) {
            clusteringResult = getClusterAssignation(geneExpresionData, centroids);
            centroids = calculateCentroids(clusteringResult, geneExpresionData);
        }

        return new GeneClusteringResult(k, clusteringResult, geneExpresionData);
    }

    double[][] generateCentroids(double maxExpressionValue, int numberOfRows, int numberOfColumns) {
        double[][] centroids = new double[numberOfRows][numberOfColumns];
        UniformDataGenerator dataGenerator = new UniformDataGenerator((int)maxExpressionValue);

        for (int i=0; i<numberOfRows; i++) {
            for (int j=0; j<numberOfColumns; j++) {
                centroids[i][j] = dataGenerator.generateRandomDouble();
            }
        }

        return centroids;
    }
    
    double getMaxExpressionValue(double[][] expressionData) {
        double maxExpression = 0.0;

        for (int i=0; i<expressionData.length; i++) {
            for (int j=0; j<expressionData[i].length; j++) {
                maxExpression = Math.max(maxExpression, expressionData[i][j]);
            }
        }

        return maxExpression;
    }

    double[][] getClusterAssignation(GeneExpressionData geneExpressionData, double[][] centroids) {
        int numberOfGenes = geneExpressionData.getExpressionData().length;
        int numberOfClusters = centroids.length;

        double[][] clusterResult = new double[numberOfGenes][numberOfClusters];

        for (int gene = 0; gene < numberOfGenes; gene++) {
            int bestCentroid = 0;
            GeneProfile geneProfile = new GeneProfile(geneExpressionData.getNumberOfReplicates(), geneExpressionData.getNumberOfTimeSeries());
            double distanceToBestCentorid = geneProfile.computeEuclideanDistance(geneExpressionData.getExpressionData()[gene], centroids[0]);
            
            for (int centroid = 1; centroid<numberOfClusters; centroid++) {
                double distanceToCurrentCentroid = geneProfile.computeEuclideanDistance(geneExpressionData.getExpressionData()[gene], centroids[centroid]);
                if (distanceToCurrentCentroid < distanceToBestCentorid) {
                    bestCentroid = centroid;
                    distanceToBestCentorid = distanceToCurrentCentroid;
                }
            }

            for (int centroid = 0; centroid<numberOfClusters; centroid++) {
                clusterResult[gene][centroid] = centroid == bestCentroid ? 1.0 : 0.0;
            }
        }

        return clusterResult;
    }

    double[][] calculateCentroids(double[][] clusteringResult, GeneExpressionData geneExpressionData) {
        double[][] centroids =  new double[this.k][geneExpressionData.getExpressionData()[0].length];

        for (int centroid = 0; centroid < this.k; centroid++) {

        }

        //TODO: Finish calculating new centroids methods
        return null;
    }
}
