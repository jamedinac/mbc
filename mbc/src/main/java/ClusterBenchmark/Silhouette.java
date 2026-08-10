package ClusterBenchmark;

import BenchmarkResult.ClusterBenchmarkResult;
import Common.*;
import Interfaces.IClusterBenchmark;
import Interfaces.IGeneDistance;
import Utilities.ClusterDataUtilities;
import Enum.BenchmarkType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Silhouette implements IClusterBenchmark {
    IGeneDistance geneDistance;

    public Silhouette(IGeneDistance  geneDistance) {
        this.geneDistance = geneDistance;
    }

    @Override
    public ClusterBenchmarkResult evaluate(GeneExpressionData geneExpressionData, GeneClusterData geneClusterData) {
        int numberOfClusters = geneClusterData.getNumberOfClusters();
        double[] silhouette = new double[geneClusterData.getNumberOfGenes()];

        if (geneExpressionData == null) {
            return new ClusterBenchmarkResult(BenchmarkType.Silhouette, silhouette, 0.0, geneClusterData);
        }

        List<ScoredGene> genes = this.collectScorableGenes(geneExpressionData, geneClusterData);
        int[] clusterSize = new int[numberOfClusters];
        for (ScoredGene gene : genes) {
            clusterSize[gene.cluster()]++;
        }

        if (genes.isEmpty()) {
            return new ClusterBenchmarkResult(BenchmarkType.Silhouette, silhouette, 0.0, geneClusterData);
        }

        double meanSilhouette = 0.0;
        for (ScoredGene current : genes) {
            double[] distanceSum = new double[numberOfClusters];

            for (ScoredGene other : genes) {
                if (other != current) {
                    distanceSum[other.cluster()] += this.geneDistance.getDistance(current.profile(), other.profile());
                }
            }

            int ownCluster = current.cluster();
            double cohesion = clusterSize[ownCluster] == 1
                    ? 0.0
                    : distanceSum[ownCluster] / (clusterSize[ownCluster] - 1);

            // Empty clusters carry no mean distance; averaging over them would yield NaN,
            // and NaN propagates through Math.min to poison every remaining score.
            double separation = Double.POSITIVE_INFINITY;
            for (int cluster = 0; cluster < numberOfClusters; cluster++) {
                if (cluster != ownCluster && clusterSize[cluster] > 0) {
                    separation = Math.min(separation, distanceSum[cluster] / clusterSize[cluster]);
                }
            }

            double score = 0.0;
            if (clusterSize[ownCluster] > 1 && Double.isFinite(separation)) {
                double denominator = Math.max(cohesion, separation);
                score = denominator == 0.0 ? 0.0 : (separation - cohesion) / denominator;
            }

            silhouette[current.outputIndex()] = score;
            meanSilhouette += score;
        }

        return new ClusterBenchmarkResult(BenchmarkType.Silhouette, silhouette, meanSilhouette / genes.size(), geneClusterData);
    }

    /**
     * Pairs each clustered gene with its expression profile by gene ID.
     *
     * <p>The cluster file also lists genes that never reached the clustering step (the basal
     * cluster), which have no row in the normalized matrix. Those genes are skipped rather
     * than indexed positionally, which would read past the end of the expression matrix.</p>
     */
    private List<ScoredGene> collectScorableGenes(GeneExpressionData geneExpressionData, GeneClusterData geneClusterData) {
        HashMap<String, Integer> expressionRowByGeneId = ClusterDataUtilities.buildExpressionRowIndex(geneExpressionData);
        List<ScoredGene> genes = new ArrayList<>();

        for (int g = 0; g < geneClusterData.getNumberOfGenes(); g++) {
            int cluster = ClusterDataUtilities.getHardClusterId(geneClusterData.getClusteringData()[g]);
            Integer expressionRow = expressionRowByGeneId.get(geneClusterData.getGeneId(g));

            if (cluster >= 0 && expressionRow != null) {
                genes.add(new ScoredGene(g, cluster, geneExpressionData.getGeneProfile(expressionRow)));
            }
        }

        return genes;
    }

    /**
     * A clustered gene that has an expression profile available.
     *
     * @param outputIndex its row in the cluster data, used to place the per-gene score
     * @param cluster     its hard cluster label
     * @param profile     its expression profile
     */
    private record ScoredGene(int outputIndex, int cluster, double[] profile) {}
}
