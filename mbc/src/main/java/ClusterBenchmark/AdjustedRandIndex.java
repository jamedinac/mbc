package ClusterBenchmark;

import BenchmarkResult.ClusterBenchmarkResult;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import Enum.BenchmarkType;
import Interfaces.IClusterBenchmark;
import Utilities.ClusterDataUtilities;

import java.util.HashMap;

/**
 * Calculates the Adjusted Rand Index (ARI) to evaluate clustering quality
 * by comparing predicted cluster assignments against a Gold Standard.
 */
public class AdjustedRandIndex implements IClusterBenchmark {

    private final GeneClusterData goldStandard;

    /**
     * Constructor receives the Gold Standard exactly as the Accuracy metric would.
     * @param goldStandard The Ground Truth assignments.
     */
    public AdjustedRandIndex(GeneClusterData goldStandard) {
        this.goldStandard = goldStandard;
    }

    @Override
    public ClusterBenchmarkResult evaluate(GeneExpressionData geneExpressionData, GeneClusterData geneClusterData) {
        // 1. Extract mapping of gene identifiers (String) to cluster IDs (Integer)
        // This gracefully handles type abstraction and enforces mapping by unique string ID.
        HashMap<String, Integer> predictedMap = ClusterDataUtilities.buildClusterMap(geneClusterData);
        HashMap<String, Integer> referenceMap = ClusterDataUtilities.buildClusterMap(goldStandard);

        // 2. Identify the common set of evaluated genes to ensure fair cross-referencing
        String[] commonGenes = ClusterDataUtilities.getCommonGenes(predictedMap, referenceMap);
        long n = commonGenes.length;

        // Base case: Not enough genes to form pairs (n < 2)
        if (n < 2) {
            return buildResult(1.0, geneClusterData);
        }

        // 3. Contingency Matrix: build efficient private mapping
        int nPred = geneClusterData.getNumberOfClusters();
        int nRef = goldStandard.getNumberOfClusters();
        int[][] contingency = ClusterDataUtilities.buildContingencyMatrix(predictedMap, referenceMap, commonGenes, nPred, nRef);

        // Calculate marginal row sums (a_i) and column sums (b_j)
        long[] a = new long[nPred];
        long[] b = new long[nRef];
        for (int i = 0; i < nPred; i++) {
            for (int j = 0; j < nRef; j++) {
                a[i] += contingency[i][j];
                b[j] += contingency[i][j];
            }
        }

        // 4. Compute sums of combinations n choose 2 avoiding Overflow
        long sumCombNij = 0; // Sum of comb(n_ij, 2)
        for (int i = 0; i < nPred; i++) {
            for (int j = 0; j < nRef; j++) {
                sumCombNij += choose2(contingency[i][j]);
            }
        }

        long sumCombAi = 0; // Sum of comb(a_i, 2)
        for (int i = 0; i < nPred; i++) {
            sumCombAi += choose2(a[i]);
        }

        long sumCombBj = 0; // Sum of comb(b_j, 2)
        for (int j = 0; j < nRef; j++) {
            sumCombBj += choose2(b[j]);
        }

        // 5. Apply the Theoretical Formula for Adjusted Rand Index
        // Using logarithms to avoid any potential overflow during multiplication
        double expectedIndex = 0.0;
        if (sumCombAi > 0 && sumCombBj > 0) {
            expectedIndex = Math.exp(Math.log(sumCombAi) + Math.log(sumCombBj) - Math.log(choose2(n)));
        }
        
        double maxIndex = 0.5 * (sumCombAi + sumCombBj);
        double index = sumCombNij;

        double denominator = maxIndex - expectedIndex;

        // Base Case & Numerical Safety: Denominator is 0.0
        // Occurs when partitions are absolutely identical or all points fall into exactly 1 partition.
        double ari;
        if (denominator == 0.0) {
            ari = 1.0; 
        } else {
            ari = (index - expectedIndex) / denominator;
        }

        return buildResult(ari, geneClusterData);
    }

    /**
     * Efficient and overflow-safe calculation of binomial coefficient (x choose 2).
     * @param x Number of items
     * @return Number of combinations (long)
     */
    private long choose2(long x) {
        if (x < 2) return 0;
        return (x * (x - 1)) / 2;
    }

    /**
     * Helper method to construct the output result.
     * Fills the perGeneMetrics array with the global ARI for compatibility.
     */
    private ClusterBenchmarkResult buildResult(double ariValue, GeneClusterData geneClusterData) {
        double[] perGeneMetrics = new double[geneClusterData.getNumberOfGenes()];
        for (int g = 0; g < perGeneMetrics.length; g++) {
            perGeneMetrics[g] = ariValue;
        }
        return new ClusterBenchmarkResult(BenchmarkType.AdjustedRandIndex, perGeneMetrics, ariValue, geneClusterData);
    }
}
