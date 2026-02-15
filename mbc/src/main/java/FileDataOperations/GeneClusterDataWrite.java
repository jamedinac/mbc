package FileDataOperations;

import Common.GeneClusterData;
import Interfaces.IGeneClusterDataWrite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GeneClusterDataWrite implements IGeneClusterDataWrite {

    @Override
    public void writeClusteringDataToFile(GeneClusterData geneClusterData, String fileName) {
        try {
            String geneClusteringResultFilePath = fileName + File.separator + "output" + ".txt";
            StringBuilder fileContent = new StringBuilder();

            for (int g = 0; g < geneClusterData.getNumberOfGenes(); g++) {
                fileContent.append(geneClusterData.getGeneId(g)).append("\t");

                for (int c = 0; c < geneClusterData.getNumberOfClusters(); c++) {
                    fileContent.append(geneClusterData.getClusteringData()[g][c]).append("\t");
                }

                fileContent.append("\n");
            }

            Files.writeString(Paths.get(geneClusteringResultFilePath), fileContent.toString());
        } catch (IOException e) {
            System.out.println("Error writing gene expression file: " + e.getMessage());
        }
    }
}
