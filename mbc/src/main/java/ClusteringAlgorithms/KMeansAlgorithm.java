package ClusteringAlgorithms;

import Common.GeneClusterData;
import Common.GeneExpressionData;
import DataGenerators.RandomGenerator;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IGeneDistance;

public class KMeansAlgorithm implements IClusteringAlgorithm {

    int k;
    int maxIterations;
    IGeneDistance geneDistance;


    public KMeansAlgorithm(int k, int maxIterations, IGeneDistance geneDistance) {
        this.k = k;
        this.maxIterations = maxIterations;
        this.geneDistance =  geneDistance;
    }

    @Override
    public GeneClusterData clusterGenes(GeneExpressionData geneExpressionData) {
        int numberOfGenes = geneExpressionData.getNumberOfGenes();

        double[][] centroids = generateCentroids(geneExpressionData);
        int[] clusterAssignation = new int[numberOfGenes];
        int restarts = 0;
        int maxRestarts = 10;

        for  (int iteration = 0; iteration < maxIterations; iteration++) {
            clusterAssignation = getClusterAssignation(geneExpressionData, centroids);
            centroids = calculateCentroids(clusterAssignation, geneExpressionData);

            if (centroids == null) {
                if (++restarts > maxRestarts) {
                    centroids = generateCentroids(geneExpressionData);
                    break;
                }
                iteration = 0;
                centroids  = generateCentroids(geneExpressionData);
            }
        }

        return new GeneClusterData(numberOfGenes, this.k, geneExpressionData.getGeneIds(), this.getClusterResultFromClusterAssignation(clusterAssignation));
    }

    double[][] generateCentroids(GeneExpressionData geneExpressionData) {
        int numberOfComponents = geneExpressionData.getNumberOfComponents();
        int numberOfGenes = geneExpressionData.getNumberOfGenes();
        double[][] centroids = new double[this.k][numberOfComponents];

        double[] minValues = new double[numberOfComponents];
        double[] maxValues = new double[numberOfComponents];

        // Initialize min and max with the first gene's profile
        if (numberOfGenes > 0) {
            double[] firstGeneProfile = geneExpressionData.getGeneProfile(0);
            System.arraycopy(firstGeneProfile, 0, minValues, 0, numberOfComponents);
            System.arraycopy(firstGeneProfile, 0, maxValues, 0, numberOfComponents);
        }

        // Find min and max for each component
        for (int i = 1; i < numberOfGenes; i++) {
            double[] geneProfile = geneExpressionData.getGeneProfile(i);
            for (int j = 0; j < numberOfComponents; j++) {
                if (geneProfile[j] < minValues[j]) {
                    minValues[j] = geneProfile[j];
                }
                if (geneProfile[j] > maxValues[j]) {
                    maxValues[j] = geneProfile[j];
                }
            }
        }

        // Generate random centroids
        for (int i = 0; i < this.k; i++) {
            for (int j = 0; j < numberOfComponents; j++) {
                double range = maxValues[j] - minValues[j];
                if (range <= 1e-9) { // Handle effectively zero range
                     centroids[i][j] = minValues[j];
                } else {
                     centroids[i][j] = RandomGenerator.uniformRandomDoubleInRange(minValues[j], maxValues[j]);
                }
            }
        }

        return centroids;
    }

    int[] getClusterAssignation(GeneExpressionData geneExpressionData, double[][] centroids) {
        int numberOfGenes = geneExpressionData.getNumberOfGenes();
        int numberOfClusters = this.k;

        int[] clusterAssignation = new int[numberOfGenes];

        for (int gene = 0; gene < numberOfGenes; gene++) {
            int bestCentroid = 0;
            double distanceToBestCentorid = this.geneDistance.getDistance(geneExpressionData.getGeneProfile(gene), centroids[0]);
            
            for (int centroid = 1; centroid < numberOfClusters; centroid++) {
                double distanceToCurrentCentroid = this.geneDistance.getDistance(geneExpressionData.getGeneProfile(gene), centroids[centroid]);

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
