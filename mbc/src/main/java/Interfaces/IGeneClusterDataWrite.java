package Interfaces;

import Common.GeneClusterData;

public interface IGeneClusterDataWrite {

    /**
     * Writes the clustering result to file name
     * @param fileName file to write the clustering data to
     */
    void writeClusteringDataToFile (GeneClusterData geneClusteringResult, String fileName);
}
