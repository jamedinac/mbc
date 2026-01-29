package ClusteringAlgorithms;

import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import GeneProfile.DoubleGeneProfile;
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

        ArrayList<DoubleGeneProfile> centroids = generateCentroids(
                getMaxExpressionValue(geneExpressionData),
                this.k,
                numberOfComponents);

        ArrayList<Integer> clusterAssignation = new ArrayList<>(numberOfGenes);

        for  (int iteration = 0; iteration < maxIterations; iteration++) {
            clusterAssignation = getClusterAssignation(geneExpressionData, centroids);
            centroids = calculateCentroids(clusterAssignation, geneExpressionData);

            // In case of empty assignation, restart the process
            if (centroids.isEmpty()) {
                centroids = generateCentroids(
                        getMaxExpressionValue(geneExpressionData),
                        this.k,
                        numberOfComponents);
                iteration = 0;
            }
        }

        return new GeneClusteringResult(k, getClusterResultFromClusterAssignation(clusterAssignation), geneExpressionData);
    }

    ArrayList<DoubleGeneProfile> generateCentroids(double maxExpressionValue, int numberOfRows, int numberOfColumns) {
        ArrayList<DoubleGeneProfile> centroids = new ArrayList<>();
        UniformDataGenerator dataGenerator = new UniformDataGenerator((int)maxExpressionValue);

        for (int i=0; i<numberOfRows; i++) {
            ArrayList<Double> centroid = new ArrayList<>(numberOfColumns);

            for (int j=0; j<numberOfColumns; j++) {
                centroid.set(j, dataGenerator.generateRandomDouble());
            }

            centroids.add(new DoubleGeneProfile(centroid, null));
        }

        return centroids;
    }
    
    double getMaxExpressionValue(GeneExpressionData expressionData) {
        double maxExpression = 0.0;

        for (var geneProfileVector : expressionData.getExpressionData()) {
            maxExpression = geneProfileVector.getMaxExpression().doubleValue();
        }

        return maxExpression;
    }

    ArrayList<Integer> getClusterAssignation(GeneExpressionData geneExpressionData, ArrayList<DoubleGeneProfile> centroids) {
        int numberOfGenes = geneExpressionData.getExpressionData().size();
        int numberOfClusters = centroids.size();

        ArrayList<Integer> clusterAssignation = new ArrayList<>(numberOfGenes);

        for (int gene = 0; gene < numberOfGenes; gene++) {
            int bestCentroid = 0;
            DoubleGeneProfile geneProfile = geneExpressionData.getGeneProfile(gene).parseToDouble();
            double distanceToBestCentorid = geneProfile.euclideanDistance(centroids.getFirst());
            
            for (int centroid = 1; centroid<numberOfClusters; centroid++) {
                geneProfile = (DoubleGeneProfile) geneExpressionData.getGeneProfile(gene).parseToDouble();
                double distanceToCurrentCentroid = geneProfile.euclideanDistance(centroids.getFirst());

                if (distanceToCurrentCentroid < distanceToBestCentorid) {
                    bestCentroid = centroid;
                    distanceToBestCentorid = distanceToCurrentCentroid;
                }
            }
            clusterAssignation.set(gene, bestCentroid);
        }

        return clusterAssignation;
    }

    ArrayList<DoubleGeneProfile> calculateCentroids(ArrayList<Integer> clusteringResult, GeneExpressionData geneExpressionData) {
        int numberOfGenes = geneExpressionData.getExpressionData().size();

        ArrayList<DoubleGeneProfile> centroids = new ArrayList<>();

        ArrayList<Integer> centroidSize = new ArrayList<>(this.k);
        Collections.fill(centroidSize, 0);

        for (int gene = 0; gene < numberOfGenes; gene++) {
            centroids.add(clusteringResult.get(gene), (DoubleGeneProfile) geneExpressionData.getGeneProfile(gene).parseToDouble());

            int currentClusterSize = centroidSize.get(gene);
            centroidSize.set(clusteringResult.get(gene), currentClusterSize + 1);
        }

        for (int centroid = 0; centroid<this.k; centroid++) {
            if (centroidSize.get(centroid) == 0) {
                return new ArrayList<>();
            }

            DoubleGeneProfile centroidDoubleGeneProfile = centroids.get(centroid);
            centroids.set(centroid, centroidDoubleGeneProfile.divide((double)centroidSize.get(centroid)));
        }

        return centroids;
    }

    ArrayList<DoubleGeneProfile> getClusterResultFromClusterAssignation (ArrayList<Integer> clusterAssignation) {
        ArrayList<DoubleGeneProfile> clusterResult = new ArrayList<>();
        for (int genes = 0; genes < clusterAssignation.size(); genes++) {
            ArrayList<Double> clusterProfileData = new ArrayList<>(this.k);
            Collections.fill(clusterProfileData, 0.0);

            for (int cluster = 0; cluster < this.k; cluster++) {
                if (clusterAssignation.get(cluster) != 0.0) {
                    clusterProfileData.set(cluster, 1.0);
                }
            }

            clusterResult.add(new DoubleGeneProfile(clusterProfileData, null));
        }

        return clusterResult;
    }
}
