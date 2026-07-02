package Utilities;

import Common.GeneClusterData;
import Enum.FilterStatus;

import java.util.Map;

/**
 * Utility class for merging filtered-out genes into the final clustering output
 * as an additional cluster (K+1).
 */
public class ClusterDataMerger {

    /**
     * Merges filtered-out genes into the clustering result as an additional cluster.
     * Clustered genes retain their original K membership values with 0.0 appended for the new cluster.
     * Filtered genes get 0.0 for all K clusters and 1.0 for the new filtered cluster (K+1).
     *
     * @param clusteredData    The clustering result containing only genes that passed filtering.
     * @param filteredOutGenes Map of gene IDs to their filter status (genes that were discarded).
     * @return A new GeneClusterData with K+1 clusters and all genes, or the original if no genes were filtered.
     */
    public static GeneClusterData mergeFilteredGenes(GeneClusterData clusteredData,
                                                      Map<String, FilterStatus> filteredOutGenes) {
        if (filteredOutGenes == null || filteredOutGenes.isEmpty()) {
            return clusteredData;
        }

        int originalK = clusteredData.getNumberOfClusters();
        int newK = originalK + 1;
        int clusteredGeneCount = clusteredData.getNumberOfGenes();
        int filteredGeneCount = filteredOutGenes.size();
        int totalGenes = clusteredGeneCount + filteredGeneCount;

        String[] mergedGeneIds = new String[totalGenes];
        double[][] mergedClusteringData = new double[totalGenes][newK];

        // Copy clustered genes: original K values + 0.0 for the new column
        for (int g = 0; g < clusteredGeneCount; g++) {
            mergedGeneIds[g] = clusteredData.getGeneId(g);
            System.arraycopy(clusteredData.getClusteringData()[g], 0, mergedClusteringData[g], 0, originalK);
            mergedClusteringData[g][originalK] = 0.0;
        }

        // Append filtered-out genes: 0.0 for all K clusters + 1.0 for the filtered cluster
        int index = clusteredGeneCount;
        for (String geneId : filteredOutGenes.keySet()) {
            mergedGeneIds[index] = geneId;
            for (int c = 0; c < originalK; c++) {
                mergedClusteringData[index][c] = 0.0;
            }
            mergedClusteringData[index][originalK] = 1.0;
            index++;
        }

        return new GeneClusterData(totalGenes, newK, mergedGeneIds, mergedClusteringData);
    }
}
