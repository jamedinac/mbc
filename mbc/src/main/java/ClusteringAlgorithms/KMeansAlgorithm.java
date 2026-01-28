package ClusteringAlgorithms;

import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import GeneProfile.GeneProfileVector;
import DataGenerators.UniformDataGenerator;
import Interfaces.IClusteringAlgorithm;

import java.util.ArrayList;
import java.util.Collections;

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
        int numberOfComponents = geneExpressionData.getNumberOfTimeSeries() * geneExpressionData.getNumberOfReplicates();

        ArrayList<GeneProfileVector> centroids = generateCentroids(
                getMaxExpressionValue(geneExpressionData.getExpressionData()),
                this.k,
                numberOfComponents);

        ArrayList<Integer> clusterAssignation = new ArrayList<>(numberOfGenes);

        for  (int iteration = 0; iteration < maxIterations; iteration++) {
            clusterAssignation = getClusterAssignation(geneExpressionData, centroids);
            centroids = calculateCentroids(clusterAssignation, geneExpressionData);

            // In case of empty assignation, restart the process
            if (centroids.isEmpty()) {
                centroids = generateCentroids(
                        getMaxExpressionValue(geneExpressionData.getExpressionData()),
                        this.k,
                        numberOfComponents);
                iteration = 0;
            }
        }

        return new GeneClusteringResult(k, getClusterResultFromClusterAssignation(clusterAssignation), geneExpressionData);
    }

    ArrayList<GeneProfileVector> generateCentroids(double maxExpressionValue, int numberOfRows, int numberOfColumns) {
        ArrayList<GeneProfileVector> centroids = new ArrayList<>();
        UniformDataGenerator dataGenerator = new UniformDataGenerator((int)maxExpressionValue);

        for (int i=0; i<numberOfRows; i++) {
            ArrayList<Double> centroid = new ArrayList<>(numberOfColumns);

            for (int j=0; j<numberOfColumns; j++) {
                centroid.set(j, dataGenerator.generateRandomDouble());
            }

            centroids.add(new GeneProfileVector(centroid));
        }

        return centroids;
    }
    
    double getMaxExpressionValue(ArrayList<GeneProfileVector> expressionData) {
        double maxExpression = 0.0;

        for (GeneProfileVector geneProfileVector : expressionData) {
            maxExpression = geneProfileVector.getMaxExpression();
        }

        return maxExpression;
    }

    ArrayList<Integer> getClusterAssignation(GeneExpressionData geneExpressionData, ArrayList<GeneProfileVector> centroids) {
        int numberOfGenes = geneExpressionData.getExpressionData().size();
        int numberOfClusters = centroids.size();

        ArrayList<Integer> clusterAssignation = new ArrayList<>(numberOfGenes);

        for (int gene = 0; gene < numberOfGenes; gene++) {
            int bestCentroid = 0;
            double distanceToBestCentorid = geneExpressionData.getGeneProfile(gene).euclideanDistance(centroids.getFirst());
            
            for (int centroid = 1; centroid<numberOfClusters; centroid++) {
                double distanceToCurrentCentroid = geneExpressionData.getGeneProfile(gene).euclideanDistance(centroids.getFirst());

                if (distanceToCurrentCentroid < distanceToBestCentorid) {
                    bestCentroid = centroid;
                    distanceToBestCentorid = distanceToCurrentCentroid;
                }
            }
            clusterAssignation.set(gene, bestCentroid);
        }

        return clusterAssignation;
    }

    ArrayList<GeneProfileVector> calculateCentroids(ArrayList<Integer> clusteringResult, GeneExpressionData geneExpressionData) {
        int numberOfGenes = geneExpressionData.getExpressionData().size();

        ArrayList<GeneProfileVector> centroids = new ArrayList<>();

        ArrayList<Integer> centroidSize = new ArrayList<>(this.k);
        Collections.fill(centroidSize, 0);

        for (int gene = 0; gene < numberOfGenes; gene++) {
            centroids.add(clusteringResult.get(gene), geneExpressionData.getGeneProfile(gene));

            int currentClusterSize = centroidSize.get(gene);
            centroidSize.set(clusteringResult.get(gene), currentClusterSize + 1);
        }

        for (int centroid = 0; centroid<this.k; centroid++) {
            if (centroidSize.get(centroid) == 0) {
                return new ArrayList<>();
            }

            GeneProfileVector centroidGeneProfileVector = centroids.get(centroid);
            centroids.set(centroid, centroidGeneProfileVector.divide((double)centroidSize.get(centroid)));
        }

        return centroids;
    }

    ArrayList<GeneProfileVector> getClusterResultFromClusterAssignation (ArrayList<Integer> clusterAssignation) {
        ArrayList<GeneProfileVector> clusterResult = new ArrayList<>();
        for (int genes = 0; genes < clusterAssignation.size(); genes++) {
            ArrayList<Double> clusterProfileData = new ArrayList<>(this.k);
            Collections.fill(clusterProfileData, 0.0);

            for (int cluster = 0; cluster < this.k; cluster++) {
                if (clusterAssignation.get(cluster) != 0.0) {
                    clusterProfileData.set(cluster, 1.0);
                }
            }

            clusterResult.add(new GeneProfileVector(clusterProfileData));
        }

        return clusterResult;
    }
}
