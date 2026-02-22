package ClusteringAlgorithms;

import Common.GeneClusterData;
import Common.GeneExpressionData;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IGeneDistance;
import Interfaces.ILinkageCriterion;

import java.util.ArrayList;
import java.util.List;

public class HierachicalClusteringAlgorithm implements IClusteringAlgorithm {

    private final int k;
    private final IGeneDistance geneDistance;
    private final ILinkageCriterion linkageCriterion;

    public HierachicalClusteringAlgorithm(int k, IGeneDistance geneDistance, ILinkageCriterion linkageCriterion) {
        this.k = k;
        this.geneDistance = geneDistance;
        this.linkageCriterion = linkageCriterion;
    }

    @Override
    public GeneClusterData clusterGenes(GeneExpressionData geneExpressionData) {
        int numberOfGenes = geneExpressionData.getNumberOfGenes();

        if (k < 1 || k > numberOfGenes) {
            throw new IllegalArgumentException(
                    "k must be between 1 and the number of genes (" + numberOfGenes + "), got " + k);
        }

        List<List<Integer>> clusters = initializeClusters(numberOfGenes);

        while (clusters.size() > k) {
            int[] closest = findClosestClusters(clusters, geneExpressionData);
            mergeClusters(clusters, closest[0], closest[1]);
        }

        int[] clusterAssignment = buildClusterAssignment(clusters, numberOfGenes);
        double[][] clusteringData = buildClusteringData(clusterAssignment, numberOfGenes);

        return new GeneClusterData(numberOfGenes, k, geneExpressionData.getGeneIds(), clusteringData);
    }

    private List<List<Integer>> initializeClusters(int numberOfGenes) {
        List<List<Integer>> clusters = new ArrayList<>(numberOfGenes);
        for (int i = 0; i < numberOfGenes; i++) {
            List<Integer> cluster = new ArrayList<>();
            cluster.add(i);
            clusters.add(cluster);
        }
        return clusters;
    }

    private int[] findClosestClusters(List<List<Integer>> clusters, GeneExpressionData data) {
        int bestI = 0;
        int bestJ = 1;
        double bestDistance = Double.MAX_VALUE;

        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                double distance = linkageCriterion.computeDistance(
                        data, geneDistance, clusters.get(i), clusters.get(j));
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestI = i;
                    bestJ = j;
                }
            }
        }

        return new int[]{bestI, bestJ};
    }

    private void mergeClusters(List<List<Integer>> clusters, int i, int j) {
        clusters.get(i).addAll(clusters.get(j));
        clusters.remove(j);
    }

    private int[] buildClusterAssignment(List<List<Integer>> clusters, int numberOfGenes) {
        int[] assignment = new int[numberOfGenes];
        for (int clusterIndex = 0; clusterIndex < clusters.size(); clusterIndex++) {
            for (int geneIndex : clusters.get(clusterIndex)) {
                assignment[geneIndex] = clusterIndex;
            }
        }
        return assignment;
    }

    private double[][] buildClusteringData(int[] clusterAssignment, int numberOfGenes) {
        double[][] clusteringData = new double[numberOfGenes][k];
        for (int gene = 0; gene < numberOfGenes; gene++) {
            clusteringData[gene][clusterAssignment[gene]] = 1.0;
        }
        return clusteringData;
    }
}
