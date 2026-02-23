package Utilities;

import Common.GeneClusterData;

import java.util.ArrayList;
import java.util.HashMap;

public class ClusterDataUtilities {

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

    public static String[] getCommonGenes(HashMap<String, Integer> predictedMap, HashMap<String, Integer> referenceMap) {
        ArrayList<String> common = new ArrayList<>();
        for (String geneId : predictedMap.keySet()) {
            if (referenceMap.containsKey(geneId)) {
                common.add(geneId);
            }
        }
        return common.toArray(new String[0]);
    }

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
