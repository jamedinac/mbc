package ClusterBenchmark;

import BenchmarkResult.ClusterBenchmarkResult;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import Enum.BenchmarkType;
import Interfaces.IClusterBenchmark;
import Utilities.ClusterDataUtilities;

import java.util.Arrays;
import java.util.HashMap;

public class Accuracy implements IClusterBenchmark {

    GeneClusterData goldStandard;

    public Accuracy(GeneClusterData goldStandard) {
        this.goldStandard = goldStandard;
    }

    @Override
    public ClusterBenchmarkResult evaluate(GeneExpressionData geneExpressionData, GeneClusterData geneClusterData) {
        HashMap<String, Integer> predictedMap = ClusterDataUtilities.buildClusterMap(geneClusterData);
        HashMap<String, Integer> referenceMap = ClusterDataUtilities.buildClusterMap(goldStandard);
        String[] commonGenes = ClusterDataUtilities.getCommonGenes(predictedMap, referenceMap);

        int nPred = geneClusterData.getNumberOfClusters();
        int nRef = goldStandard.getNumberOfClusters();
        int[][] contingency = ClusterDataUtilities.buildContingencyMatrix(predictedMap, referenceMap, commonGenes, nPred, nRef);

        int maxDim = Math.max(nPred, nRef);
        int[][] costMatrix = new int[maxDim][maxDim];
        int maxVal = commonGenes.length;
        for (int i = 0; i < maxDim; i++) {
            for (int j = 0; j < maxDim; j++) {
                if (i < nPred && j < nRef) {
                    costMatrix[i][j] = maxVal - contingency[i][j];
                } else {
                    costMatrix[i][j] = maxVal;
                }
            }
        }

        int[] assignment = hungarian(costMatrix);

        // Build mapping from predicted cluster to reference cluster
        int[] clusterMapping = new int[nPred];
        Arrays.fill(clusterMapping, -1);
        for (int i = 0; i < nPred; i++) {
            if (assignment[i] < nRef) {
                clusterMapping[i] = assignment[i];
            }
        }

        // Compute per-gene accuracy
        double[] perGeneAccuracy = new double[geneClusterData.getNumberOfGenes()];
        int correctCount = 0;
        for (int g = 0; g < geneClusterData.getNumberOfGenes(); g++) {
            String geneId = geneClusterData.getGeneId(g);
            if (predictedMap.containsKey(geneId) && referenceMap.containsKey(geneId)) {
                int predCluster = predictedMap.get(geneId);
                int refCluster = referenceMap.get(geneId);
                if (clusterMapping[predCluster] == refCluster) {
                    perGeneAccuracy[g] = 1.0;
                    correctCount++;
                }
            }
        }

        double accuracy = commonGenes.length == 0 ? 0.0 : (double) correctCount / commonGenes.length;

        return new ClusterBenchmarkResult(BenchmarkType.Accuracy, perGeneAccuracy, accuracy, geneClusterData);
    }

    private int[] hungarian(int[][] costMatrix) {
        int n = costMatrix.length;
        int[] u = new int[n + 1];
        int[] v = new int[n + 1];
        int[] p = new int[n + 1];
        int[] way = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            p[0] = i;
            int j0 = 0;
            int[] minv = new int[n + 1];
            boolean[] used = new boolean[n + 1];
            Arrays.fill(minv, Integer.MAX_VALUE);

            do {
                used[j0] = true;
                int i0 = p[j0];
                int delta = Integer.MAX_VALUE;
                int j1 = -1;

                for (int j = 1; j <= n; j++) {
                    if (!used[j]) {
                        int cur = costMatrix[i0 - 1][j - 1] - u[i0] - v[j];
                        if (cur < minv[j]) {
                            minv[j] = cur;
                            way[j] = j0;
                        }
                        if (minv[j] < delta) {
                            delta = minv[j];
                            j1 = j;
                        }
                    }
                }

                for (int j = 0; j <= n; j++) {
                    if (used[j]) {
                        u[p[j]] += delta;
                        v[j] -= delta;
                    } else {
                        minv[j] -= delta;
                    }
                }

                j0 = j1;
            } while (p[j0] != 0);

            do {
                int j1 = way[j0];
                p[j0] = p[j1];
                j0 = j1;
            } while (j0 != 0);
        }

        // p[j] = row assigned to column j; invert to get column assigned to row
        int[] result = new int[n];
        for (int j = 1; j <= n; j++) {
            result[p[j] - 1] = j - 1;
        }
        return result;
    }
}
