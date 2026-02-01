package Interfaces;

import Common.GeneClusteringResult;

public interface IGeneExpressionDataWrite {

    /**
     * Writes the clustering result to file name
     * @param fileName file to write the clustering data to
     */
    public void writeClusteringDataToFile (GeneClusteringResult geneClusteringResult, String fileName);
}
