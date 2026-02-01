package GeneExpressionDataOperation;

import Common.GeneExpressionData;
import GeneFilter.CompositeFilter;
import GeneFilter.GeneFilterByTotalExpression;
import Interfaces.IGeneExpressionDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class GeneExpressionDataLoad implements IGeneExpressionDataSource {

    private String geneCountDirectoryPath;
    private CompositeFilter geneFilters;

    public GeneExpressionDataLoad(String geneCountDirectoryPath) {
        this.geneCountDirectoryPath = geneCountDirectoryPath;
        this.geneFilters = new CompositeFilter();
    }

    public GeneExpressionData getGeneExpressionFormattedData (int numberOfTimeSeries, int numberOfReplicates) {
        String geneExpressionFile = geneCountDirectoryPath + "\\data.txt";
        String[] geneExpressionFileLines = this.getFileLines(geneExpressionFile);

        geneFilters.addfilter(new GeneFilterByTotalExpression(1000));

        int numberOfGenes = getNumberOfFilteredGenes(geneExpressionFileLines);
        int numberOfComponents = numberOfReplicates * numberOfTimeSeries;

        double[][] expressionData = new double[numberOfGenes][numberOfComponents];
        String[] metadata = new String[numberOfComponents];
        String[] geneIds = new String[numberOfGenes];

        int currentGene = 0;
        for (int row = 1; row < geneExpressionFileLines.length; row++) {
            if (geneFilters.filterGene(geneExpressionFileLines[row])) {
                String[] dataRow = geneExpressionFileLines[row].split("\t");

                geneIds[currentGene] = dataRow[0];
                for (int c = 1; c <= numberOfComponents; c++) {
                    expressionData[currentGene][c - 1] = Double.parseDouble(dataRow[c]);
                }

                currentGene++;
            }
        }

        return new GeneExpressionData(numberOfGenes,
                numberOfReplicates,
                numberOfTimeSeries,
                this.normalizeExpressionData(expressionData),
                metadata,
                geneIds);
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
        int numberOfComponents = expressionData[0].length;

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

    private int getNumberOfFilteredGenes (String[] geneExpressionFileLines) {
        int numberOfRows =  geneExpressionFileLines.length;
        int numberOfFilteredGenes = 0;

        for (int r = 1; r < numberOfRows; r++) {
            if (geneFilters.filterGene(geneExpressionFileLines[r])) {
                numberOfFilteredGenes++;
            }
        }

        return numberOfFilteredGenes;
    }
}
