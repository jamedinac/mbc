package ClusteringAlgorithms;

import Common.ClusteringAlgorithm;
import Common.GeneClusteringResult;
import Common.GeneExpressionData;
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
    public GeneClusteringResult clusterGenes(GeneExpressionData geneExpressionData) {
        int numberOfGenes = geneExpressionData.getNumberOfGenes();
        int numberOfComponents = geneExpressionData.getNumberOfComponents();

        double[][] centroids = generateCentroids(geneExpressionData);
        int[] clusterAssignation = new int[numberOfGenes];

        for  (int iteration = 0; iteration < maxIterations; iteration++) {
            clusterAssignation = getClusterAssignation(geneExpressionData, centroids);
            centroids = calculateCentroids(clusterAssignation, geneExpressionData);

            // In case of empty assignation, restart the process
            if (centroids == null) {
                centroids = generateCentroids(geneExpressionData);
                iteration = 0;
            }
        }

        return new GeneClusteringResult(k, getClusterResultFromClusterAssignation(clusterAssignation), geneExpressionData, ClusteringAlgorithm.Kmeans);
    }

    double[][] generateCentroids(GeneExpressionData geneExpressionData) {
        double[][] centroids = new double[this.k][geneExpressionData.getNumberOfComponents()];
        UniformDataGenerator dataGenerator = new UniformDataGenerator();

        for (int i=0; i < this.k; i++) {
            centroids[i] = geneExpressionData.getGeneProfile(dataGenerator.generateRandomInt(geneExpressionData.getNumberOfGenes()));
        }

        return centroids;
    }

    int[] getClusterAssignation(GeneExpressionData geneExpressionData, double[][] centroids) {
        int numberOfGenes = geneExpressionData.getNumberOfGenes();
        int numberOfClusters = this.k;

        int[] clusterAssignation = new int[numberOfGenes];

        for (int gene = 0; gene < numberOfGenes; gene++) {
            int bestCentroid = 0;
            double distanceToBestCentorid = this.euclideanDistance(geneExpressionData.getGeneProfile(gene), centroids[0]);
            
            for (int centroid = 1; centroid < numberOfClusters; centroid++) {
                double distanceToCurrentCentroid = euclideanDistance(geneExpressionData.getGeneProfile(gene), centroids[centroid]);

                if (distanceToCurrentCentroid < distanceToBestCentorid) {
                    bestCentroid = centroid;
                    distanceToBestCentorid = distanceToCurrentCentroid;
                }
            }
            clusterAssignation[gene] = bestCentroid;;
        }

        return clusterAssignation;
    }

    double[][] calculateCentroids(int[] clusteringResult, GeneExpressionData geneExpressionData) {
        int numberOfGenes = geneExpressionData.getNumberOfGenes();
        int numberOfComponents = geneExpressionData.getNumberOfComponents();

        double[][] centroids = new double[this.k][numberOfComponents];

        int[] centroidSize = new int[this.k];

        for (int gene = 0; gene < numberOfGenes; gene++) {
            int c = clusteringResult[gene];
            centroids[c] = add(centroids[c], geneExpressionData.getGeneProfile(gene));
            centroidSize[c]++;
        }

        for (int centroid = 0; centroid < this.k; centroid++) {
            if (centroidSize[centroid] == 0) {
                return null;
            }
        }

        for (int c = 0; c < this.k; c++) {
            centroids[c] = this.divide(centroids[c], centroidSize[c]);
        }
        return centroids;
    }

    double[][] getClusterResultFromClusterAssignation (int[] clusterAssignation) {
        int numberOfGenes = clusterAssignation.length;
        double[][] clusterResult = new double[numberOfGenes][this.k];

        for (int gene = 0; gene < numberOfGenes; gene++) {
            clusterResult[gene][clusterAssignation[gene]] = 1.0;
        }

        return clusterResult;
    }

    double euclideanDistance(double[] geneExpressionData, double[] centroid) {
        double distance = 0.0;
        int numberOfComponents = geneExpressionData.length;

        for (int componentIndex = 0; componentIndex < numberOfComponents; componentIndex++) {
            double difference = geneExpressionData[componentIndex] - centroid[componentIndex];
            distance += difference * difference;
        }

        return Math.sqrt(distance);
    }

    double[] add(double[] centroid, double[] geneExpressionData) {
        for (int i = 0; i < geneExpressionData.length; i++) {
            centroid[i] += geneExpressionData[i];
        }
        return centroid;
    }

    double[] divide(double[] centroid, double m) {
        for (int c = 0; c < centroid.length; c++) {
            centroid[c] /= m;
        }
        return centroid;
    }
}
