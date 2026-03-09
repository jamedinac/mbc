package Interfaces;

import Common.GeneExpressionData;
import java.util.List;

/**
 * Interface for calculating the distance between two clusters in hierarchical clustering.
 */
public interface ILinkageCriterion {

    /**
     * Computes the distance between two sets of genes (clusters) based on a specific linkage strategy.
     * 
     * @param data the complete gene expression matrix
     * @param geneDistance the distance metric used to calculate pairwise distances
     * @param clusterA the list of gene indices belonging to the first cluster
     * @param clusterB the list of gene indices belonging to the second cluster
     * @return the aggregated distance between the two clusters
     */
    double computeDistance(GeneExpressionData data, IGeneDistance geneDistance,
                           List<Integer> clusterA, List<Integer> clusterB);
}
