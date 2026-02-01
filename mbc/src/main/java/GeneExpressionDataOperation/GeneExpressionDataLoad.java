package GeneExpressionDataOperation;

import Common.GeneExpressionData;
import Interfaces.IGeneExpressionDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class GeneExpressionDataLoad implements IGeneExpressionDataSource {

    private String geneCountDirectoryPath;

    public GeneExpressionDataLoad(String geneCountDirectoryPath) {
        this.geneCountDirectoryPath = geneCountDirectoryPath;
    }

    public GeneExpressionData getGeneExpressionFormattedData () {
        String geneExpressionFile = geneCountDirectoryPath + "\\data.txt";

        String[] geneExpressionFileLines = getFileLines(geneExpressionFile);

        int numberOfGenes = 10;
        int numberOfReplicates = 10;
        int numberOfTimeSeries = 1;
        int numberOfComponents = numberOfReplicates * numberOfTimeSeries;

        double[][] expressionData = new double[numberOfGenes][numberOfComponents];
        String[] metadata = new String[numberOfComponents];
        String[] geneIds = new String[numberOfGenes];

        for (int row = 1; row <= numberOfGenes; row++) {
            String[] dataRow = geneExpressionFileLines[row].split("\t");

            geneIds[row-1] = dataRow[0];
            for (int c = 1; c <= numberOfComponents; c++) {
                expressionData[row - 1][c - 1] = Double.parseDouble(dataRow[c]);
            }
        }

        return new GeneExpressionData(numberOfGenes, numberOfReplicates, numberOfTimeSeries, normalizeExpressionData(expressionData), metadata, geneIds);
    }

    private String[] getFileLines (String fileName) {
        String[] fileLines = null;

        try {
            fileLines = Files.readAllLines(Paths.get(fileName)).toArray(new String[0]);
        } catch (IOException e) {
            System.out.println("Error reading gene expression file: " + e.getMessage());
        }

        return fileLines;
    }

    private double[][] normalizeExpressionData(double[][] expressionData) {
        int numberOfGenes = expressionData.length;
        int  numberOfComponents = expressionData[0].length;

        double maxExpressionValue = 0.0;

        for (double[] expressionDataRow : expressionData) {
            for (double expressionDataValue : expressionDataRow) {
                maxExpressionValue = Math.max(maxExpressionValue, expressionDataValue);
            }
        }

        for (int g = 0; g < numberOfGenes; ++g) {
            for (int c = 0; c < numberOfComponents; c++) {
                expressionData[g][c] = expressionData[g][c] / maxExpressionValue;
            }
        }

        return expressionData;
    }
}
