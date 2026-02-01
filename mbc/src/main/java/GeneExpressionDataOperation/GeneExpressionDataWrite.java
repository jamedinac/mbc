package GeneExpressionDataOperation;

import Common.GeneClusteringResult;
import Interfaces.IGeneExpressionDataWrite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GeneExpressionDataWrite implements IGeneExpressionDataWrite {

    @Override
    public void writeClusteringDataToFile(GeneClusteringResult geneClusteringResult, String fileName) {
        try {
            File file = new File(fileName);

            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            writer.write("Hello World!");

        } catch (IOException e) {
            System.out.println("Error writing gene expression file: " + e.getMessage());
        }
    }
}
