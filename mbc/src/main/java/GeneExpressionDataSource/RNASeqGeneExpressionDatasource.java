package GeneExpressionDataSource;

import Common.GeneExpressionData;
import Interfaces.IGeneExpressionDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class RNASeqGeneExpressionDatasource implements IGeneExpressionDataSource {

    private String geneCountDirectoryPath;

    public RNASeqGeneExpressionDatasource(String geneCountDirectoryPath) {
        this.geneCountDirectoryPath = geneCountDirectoryPath;
    }

    public GeneExpressionData getGeneExpressionFormattedData () {
        try {
            Path filePath = Paths.get(geneCountDirectoryPath, "data.txt");

            List<String> lines = Files.readAllLines(filePath);

            bool isFirstLine = true;
            for (String line : lines) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
