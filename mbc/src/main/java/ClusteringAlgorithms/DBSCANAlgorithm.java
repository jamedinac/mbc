package ClusteringAlgorithms;

import Common.GeneClusterData;
import Common.GeneExpressionData;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IGeneDistance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBSCANAlgorithm implements IClusteringAlgorithm {

    private static final int UNVISITED = -1;
    private static final int NOISE = -2;

    private final double eps;
    private final int minPts;
    private final IGeneDistance geneDistance;

    public DBSCANAlgorithm(double eps, int minPts, IGeneDistance geneDistance) {
        this.eps = eps;
        this.minPts = minPts;
        this.geneDistance = geneDistance;
    }

    @Override
    public GeneClusterData clusterGenes(GeneExpressionData geneExpressionData) {
        if (eps <= 0) {
            throw new IllegalArgumentException("eps must be positive, got " + eps);
        }
        if (minPts < 1) {
            throw new IllegalArgumentException("minPts must be at least 1, got " + minPts);
        }

        int numberOfGenes = geneExpressionData.getNumberOfGenes();
        int[] labels = new int[numberOfGenes];
        for (int i = 0; i < numberOfGenes; i++) {
            labels[i] = UNVISITED;
        }

        int currentCluster = 0;

        for (int geneIndex = 0; geneIndex < numberOfGenes; geneIndex++) {
            if (labels[geneIndex] != UNVISITED) {
                continue;
            }

            List<Integer> neighbors = regionQuery(geneExpressionData, geneIndex);

            if (neighbors.size() < minPts) {
                labels[geneIndex] = NOISE;
            } else {
                expandCluster(geneExpressionData, labels, geneIndex, neighbors, currentCluster);
                currentCluster++;
            }
        }

        return buildClusterData(geneExpressionData, labels, currentCluster);
    }

    private List<Integer> regionQuery(GeneExpressionData geneExpressionData, int geneIndex) {
        List<Integer> neighbors = new ArrayList<>();
        int numberOfGenes = geneExpressionData.getNumberOfGenes();
        double[] geneProfile = geneExpressionData.getGeneProfile(geneIndex);

        for (int i = 0; i < numberOfGenes; i++) {
            if (geneDistance.getDistance(geneProfile, geneExpressionData.getGeneProfile(i)) <= eps) {
                neighbors.add(i);
            }
        }

        return neighbors;
    }

    private void expandCluster(
            GeneExpressionData geneExpressionData,
            int[] labels,
            int geneIndex,
            List<Integer> neighbors,
            int clusterId) {

        labels[geneIndex] = clusterId;

        Set<Integer> inSeedList = new HashSet<>(neighbors);

        int i = 0;
        while (i < neighbors.size()) {
            int neighborIndex = neighbors.get(i);

            if (labels[neighborIndex] == NOISE) {
                labels[neighborIndex] = clusterId;
            }

            if (labels[neighborIndex] == UNVISITED) {
                labels[neighborIndex] = clusterId;

                List<Integer> neighborNeighbors = regionQuery(geneExpressionData, neighborIndex);

                if (neighborNeighbors.size() >= minPts) {
                    for (int nn : neighborNeighbors) {
                        if (inSeedList.add(nn)) {
                            neighbors.add(nn);
                        }
                    }
                }
            }

            i++;
        }
    }

    private GeneClusterData buildClusterData(
            GeneExpressionData geneExpressionData,
            int[] labels,
            int numberOfClusters) {

        int clusteredGeneCount = 0;
        for (int label : labels) {
            if (label >= 0) {
                clusteredGeneCount++;
            }
        }

        if (clusteredGeneCount == 0 || numberOfClusters == 0) {
            return new GeneClusterData(0, 0, new String[0], new double[0][0]);
        }

        String[] clusteredGeneIds = new String[clusteredGeneCount];
        double[][] clusteringData = new double[clusteredGeneCount][numberOfClusters];

        int outputIndex = 0;
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] >= 0) {
                clusteredGeneIds[outputIndex] = geneExpressionData.getGeneId(i);
                clusteringData[outputIndex][labels[i]] = 1.0;
                outputIndex++;
            }
        }

        return new GeneClusterData(clusteredGeneCount, numberOfClusters, clusteredGeneIds, clusteringData);
    }
}
