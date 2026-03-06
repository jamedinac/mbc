package ClusterBenchmark;

import BenchmarkResult.ClusterBenchmarkResult;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import Enum.BenchmarkType;
import Interfaces.IClusterBenchmark;
import Utilities.ClusterDataUtilities;

import java.util.HashMap;

public class NMI implements IClusterBenchmark {

    GeneClusterData goldStandard;

    public NMI(GeneClusterData goldStandard) {
        this.goldStandard = goldStandard;
    }

    @Override
    public ClusterBenchmarkResult evaluate(GeneExpressionData geneExpressionData, GeneClusterData geneClusterData) {
        HashMap<String, Integer> predictedMap = ClusterDataUtilities.buildClusterMap(geneClusterData);
        HashMap<String, Integer> referenceMap = ClusterDataUtilities.buildClusterMap(goldStandard);
        String[] commonGenes = ClusterDataUtilities.getCommonGenes(predictedMap, referenceMap);

        int n = commonGenes.length;
        if (n == 0) {
            return new ClusterBenchmarkResult(BenchmarkType.NMI, new double[geneClusterData.getNumberOfGenes()], 0.0, geneClusterData);
        }

        int nPred = geneClusterData.getNumberOfClusters();
        int nRef = goldStandard.getNumberOfClusters();
        int[][] contingency = ClusterDataUtilities.buildContingencyMatrix(predictedMap, referenceMap, commonGenes, nPred, nRef);

        // Compute marginals
        int[] rowSums = new int[nPred];
        int[] colSums = new int[nRef];
        for (int i = 0; i < nPred; i++) {
            for (int j = 0; j < nRef; j++) {
                rowSums[i] += contingency[i][j];
                colSums[j] += contingency[i][j];
            }
        }

        // H(U) - entropy of predicted clustering
        double hu = 0.0;
        for (int i = 0; i < nPred; i++) {
            if (rowSums[i] > 0) {
                double p = (double) rowSums[i] / n;
                hu -= p * Math.log(p);
            }
        }

        // H(V) - entropy of reference clustering
        double hv = 0.0;
        for (int j = 0; j < nRef; j++) {
            if (colSums[j] > 0) {
                double p = (double) colSums[j] / n;
                hv -= p * Math.log(p);
            }
        }

        // I(U;V) - mutual information
        double mi = 0.0;
        for (int i = 0; i < nPred; i++) {
            for (int j = 0; j < nRef; j++) {
                if (contingency[i][j] > 0) {
                    double pij = (double) contingency[i][j] / n;
                    double pi = (double) rowSums[i] / n;
                    double pj = (double) colSums[j] / n;
                    mi += pij * Math.log(pij / (pi * pj));
                }
            }
        }

        double nmi = Math.max(hu, hv) == 0.0 ? 0.0 : mi / Math.max(hu, hv);

        return new ClusterBenchmarkResult(BenchmarkType.NMI, new double[geneClusterData.getNumberOfGenes()], nmi, geneClusterData);
    }
}
