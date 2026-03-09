package Interfaces;

import Common.GeneClusterData;

/**
 * Interface for exporting clustering results to an external file.
 */
public interface IGeneClusterDataWrite {

    /**
     * Writes the clustering result to the specified file.
     * 
     * @param geneClusteringResult the resulting cluster assignments to write
     * @param fileName the path and name of the file to write the data to
     */
    void writeClusteringDataToFile(GeneClusterData geneClusteringResult, String fileName);
}
