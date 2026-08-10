package ClusterBenchmark;

import Enum.BenchmarkType;
import BenchmarkResult.ClusterBenchmarkResult;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import Interfaces.IClusterBenchmark;
import Interfaces.IGeneDistance;
import Utilities.ClusterDataUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WCSS implements IClusterBenchmark {
    IGeneDistance geneDistance;

    public WCSS(IGeneDistance geneDistance) {
        this.geneDistance = geneDistance;
    }

    @Override
    public ClusterBenchmarkResult evaluate(GeneExpressionData geneExpressionData, GeneClusterData geneClusterData) {
        double[] perGeneValues = new double[geneClusterData.getNumberOfGenes()];

        if (geneExpressionData == null) {
            return new ClusterBenchmarkResult(BenchmarkType.WCSS, perGeneValues, 0.0, geneClusterData);
        }

        List<ClusteredGene> genes = this.collectClusteredGenes(geneExpressionData, geneClusterData);
        if (genes.isEmpty()) {
            return new ClusterBenchmarkResult(BenchmarkType.WCSS, perGeneValues, 0.0, geneClusterData);
        }

        double[][] centroids = this.getCentroids(
                genes, geneClusterData.getNumberOfClusters(), geneExpressionData.getNumberOfComponents());

        double sum = 0.0;
        for (ClusteredGene gene : genes) {
            double distance = this.geneDistance.getDistance(centroids[gene.cluster()], gene.profile());
            double squaredDistance = distance * distance;

            perGeneValues[gene.outputIndex()] = squaredDistance;
            sum += squaredDistance;
        }

        return new ClusterBenchmarkResult(BenchmarkType.WCSS, perGeneValues, sum, geneClusterData);
    }

    /**
     * Pairs each clustered gene with its expression profile by gene ID.
     *
     * <p>The cluster file also lists genes that never reached the clustering step (the basal
     * cluster), which have no row in the normalized matrix. Those genes are skipped rather
     * than indexed positionally, which would pair a gene ID with another gene's profile.</p>
     */
    private List<ClusteredGene> collectClusteredGenes(GeneExpressionData geneExpressionData, GeneClusterData geneClusterData) {
        HashMap<String, Integer> expressionRowByGeneId = ClusterDataUtilities.buildExpressionRowIndex(geneExpressionData);
        List<ClusteredGene> genes = new ArrayList<>();

        for (int g = 0; g < geneClusterData.getNumberOfGenes(); g++) {
            int cluster = ClusterDataUtilities.getHardClusterId(geneClusterData.getClusteringData()[g]);
            Integer expressionRow = expressionRowByGeneId.get(geneClusterData.getGeneId(g));

            if (cluster >= 0 && expressionRow != null) {
                genes.add(new ClusteredGene(g, cluster, geneExpressionData.getGeneProfile(expressionRow)));
            }
        }

        return genes;
    }

    private double[][] getCentroids(List<ClusteredGene> genes, int numberOfClusters, int numberOfComponents) {
        double[][] centroids = new double[numberOfClusters][numberOfComponents];
        int[] clusterSize = new int[numberOfClusters];

        for (ClusteredGene gene : genes) {
            double[] profile = gene.profile();
            for (int i = 0; i < numberOfComponents; i++) {
                centroids[gene.cluster()][i] += profile[i];
            }
            clusterSize[gene.cluster()]++;
        }

        for (int c = 0; c < numberOfClusters; c++) {
            if (clusterSize[c] == 0) {
                continue;
            }
            for (int i = 0; i < numberOfComponents; i++) {
                centroids[c][i] /= clusterSize[c];
            }
        }

        return centroids;
    }

    /**
     * A clustered gene that has an expression profile available.
     *
     * @param outputIndex its row in the cluster data, used to place the per-gene value
     * @param cluster     its hard cluster label
     * @param profile     its expression profile
     */
    private record ClusteredGene(int outputIndex, int cluster, double[] profile) {}
}
