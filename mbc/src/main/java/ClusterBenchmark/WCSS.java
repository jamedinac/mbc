package ClusterBenchmark;

import Common.BenchmarkType;
import Common.ClusterBenchmarkResult;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import Interfaces.IClusterBenchmark;
import Interfaces.IGeneDistance;

import java.util.HashMap;

public class WCSS implements IClusterBenchmark {
    IGeneDistance geneDistance;
    HashMap<String, Integer> geneCluster = new HashMap<>();


    public WCSS(IGeneDistance geneDistance) {
        this.geneDistance = geneDistance;
    }

    @Override
    public ClusterBenchmarkResult evaluate(GeneExpressionData geneExpressionData, GeneClusterData geneClusterData) {
        double[][] centroids = this.getCentroids(geneExpressionData, geneClusterData);

        double sum = 0.0;
        for (int g=0; g<geneExpressionData.getNumberOfGenes(); g++) {
            int ki = geneCluster.get(geneExpressionData.getGeneId(g));
            double distance = this.geneDistance.getDistance(centroids[ki], geneExpressionData.getGeneProfile(g));
            sum += Math.abs(distance);

        }

        return new ClusterBenchmarkResult(BenchmarkType.WCSS, new double[0], sum, geneClusterData);
    }

    private double[][] getCentroids(GeneExpressionData geneExpressionData, GeneClusterData geneClusterData) {
        double[][] centroids = new double[geneClusterData.getNumberOfClusters()][geneExpressionData.getNumberOfComponents()];
        int[] clusterSize =  new int[geneClusterData.getNumberOfClusters()];

        for (int g=0; g<geneClusterData.getNumberOfGenes(); g++) {
            geneCluster.put(geneClusterData.getGeneId(g), this.getGeneCluster(g, geneClusterData));
        }

        for (int g=0; g<geneExpressionData.getNumberOfGenes(); g++) {
            int ki = geneCluster.get(geneClusterData.getGeneId(g));
            centroids[ki] = this.addGenes(centroids[ki], geneExpressionData.getGeneProfile(g));
            clusterSize[ki]++;
        }

        for (int c=0; c<centroids.length; c++) {
            centroids[c] = this.divideCentroid(centroids[c],  clusterSize[c]);
        }

        return centroids;
    }
    private int getGeneCluster(int g, GeneClusterData geneClusterData) {
        for (int c=0; c<geneClusterData.getNumberOfClusters(); c++) {
            if (geneClusterData.getClusteringData()[g][c] == 1.0) {
                return c;
            }
        }
        return -1;
    }

    private double[] addGenes(double[] centroid, double[] geneProfile) {
        double[] ans = new  double[geneProfile.length];
        for (int i = 0; i < geneProfile.length; i++) {
            ans[i] = centroid[i] + geneProfile[i];
        }
        return ans;
    }

    private double[] divideCentroid(double[] centroid, double d) {
        double[] ans = new double[centroid.length];
        for (int i = 0; i < centroid.length; i++) {
            ans[i] = centroid[i] / d;
        }
        return ans;
    }
}
