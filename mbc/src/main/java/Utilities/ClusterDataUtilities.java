package Utilities;

import Common.GeneClusterData;
import Common.GeneExpressionData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility class for manipulating and analyzing gene clustering data structures.
 */
public class ClusterDataUtilities {

    /**
     * Resolves the hard cluster label of a membership row: the index holding the highest
     * membership value.
     *
     * <p>Soft algorithms such as Fuzzy C-Means emit fractional memberships that are never
     * exactly 1.0, so testing for equality against 1.0 would leave every gene unassigned.
     * Taking the argmax handles hard and soft assignments alike.</p>
     *
     * @param membership the membership values of one gene across all clusters
     * @return the index of the highest membership, or -1 when no cluster has a positive value
     */
    public static int getHardClusterId(double[] membership) {
        int bestCluster = -1;
        double bestMembership = 0.0;

        for (int c = 0; c < membership.length; c++) {
            if (membership[c] > bestMembership) {
                bestMembership = membership[c];
                bestCluster = c;
            }
        }

        return bestCluster;
    }

    /**
     * Indexes the rows of an expression matrix by gene ID.
     *
     * <p>Cluster results and expression matrices do not share an ordering or a length: the
     * cluster file also lists genes that never reached the clustering step (the basal
     * cluster). Metrics that need both must pair them by identity, never by position.</p>
     *
     * @param data the expression matrix to index
     * @return a HashMap from gene ID to its row index in the expression matrix
     */
    public static HashMap<String, Integer> buildExpressionRowIndex(GeneExpressionData data) {
        HashMap<String, Integer> index = new HashMap<>();
        for (int row = 0; row < data.getNumberOfGenes(); row++) {
            index.put(data.getGeneId(row), row);
        }
        return index;
    }

    /**
     * Builds a map connecting a gene ID to its assigned cluster index.
     *
     * @param data the complete gene clustering data
     * @return a HashMap where keys are gene IDs and values are cluster indices
     */
    public static HashMap<String, Integer> buildClusterMap(GeneClusterData data) {
        HashMap<String, Integer> map = new HashMap<>();
        for (int g = 0; g < data.getNumberOfGenes(); g++) {
            int cluster = getHardClusterId(data.getClusteringData()[g]);
            if (cluster >= 0) {
                map.put(data.getGeneId(g), cluster);
            }
        }
        return map;
    }

    /**
     * Finds genes that exist in both predicted and reference cluster maps.
     * 
     * @param predictedMap the map of predicted cluster assignments
     * @param referenceMap the map of reference (gold standard) cluster assignments
     * @return an array of gene IDs present in both maps
     */
    public static String[] getCommonGenes(HashMap<String, Integer> predictedMap, HashMap<String, Integer> referenceMap) {
        ArrayList<String> common = new ArrayList<>();
        for (String geneId : predictedMap.keySet()) {
            if (referenceMap.containsKey(geneId)) {
                common.add(geneId);
            }
        }
        return common.toArray(new String[0]);
    }

    /**
     * Builds a contingency matrix (confusion matrix) comparing predicted clusters against reference clusters.
     * 
     * @param predictedMap the map of predicted cluster assignments
     * @param referenceMap the map of reference cluster assignments
     * @param commonGenes the list of genes shared between both maps
     * @param nPredClusters the total number of predicted clusters
     * @param nRefClusters the total number of reference clusters
     * @return a 2D integer array representing the contingency matrix
     */
    public static int[][] buildContingencyMatrix(HashMap<String, Integer> predictedMap, HashMap<String, Integer> referenceMap,
                                                  String[] commonGenes, int nPredClusters, int nRefClusters) {
        int[][] contingency = new int[nPredClusters][nRefClusters];
        for (String gene : commonGenes) {
            int predCluster = predictedMap.get(gene);
            int refCluster = referenceMap.get(gene);
            contingency[predCluster][refCluster]++;
        }
        return contingency;
    }
}
