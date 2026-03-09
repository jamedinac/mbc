package Utilities;

import Common.GeneClusterData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility class for manipulating and analyzing gene clustering data structures.
 */
public class ClusterDataUtilities {

    /**
     * Builds a map connecting a gene ID to its assigned cluster index.
     * 
     * @param data the complete gene clustering data
     * @return a HashMap where keys are gene IDs and values are cluster indices
     */
    public static HashMap<String, Integer> buildClusterMap(GeneClusterData data) {
        HashMap<String, Integer> map = new HashMap<>();
        for (int g = 0; g < data.getNumberOfGenes(); g++) {
            for (int c = 0; c < data.getNumberOfClusters(); c++) {
                if (data.getClusteringData()[g][c] == 1.0) {
                    map.put(data.getGeneId(g), c);
                    break;
                }
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
