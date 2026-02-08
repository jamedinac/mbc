package FileDataOperations;

import Common.GeneClusteringResult;
import Interfaces.IGeneExpressionDataWrite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GeneExpressionDataWrite implements IGeneExpressionDataWrite {

    @Override
    public void writeClusteringDataToFile(GeneClusteringResult geneClusteringResult, String fileName) {
        try {
            String geneClusteringResultFilePath = fileName + File.separator + "output_" + geneClusteringResult.getClusteringAlgorithm() + ".txt";
            StringBuilder fileContent = new StringBuilder();

            for (int g = 0; g < geneClusteringResult.getNumberOfGenes(); g++) {
                fileContent.append(geneClusteringResult.getGeneExpressionData().getGeneId(g)).append("\t");

                for (int c = 0; c < geneClusteringResult.getNumberOfClusters(); c++) {
                    fileContent.append(geneClusteringResult.getGeneClusteringData()[g][c]).append("\t");
                }

                fileContent.append("\n");
            }

            Files.writeString(Paths.get(geneClusteringResultFilePath), fileContent.toString());
        } catch (IOException e) {
            System.out.println("Error writing gene expression file: " + e.getMessage());
        }
    }
}
