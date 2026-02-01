package GeneExpressionDataOperation;

import Common.GeneExpressionData;
import Common.SampleMetadata;
import GeneFilter.CompositeFilter;
import GeneFilter.GeneFilterByTotalExpression;
import GeneFilter.GeneFilterByVariance;
import Interfaces.IGeneExpressionDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;


public class GeneExpressionDataLoad implements IGeneExpressionDataSource {

    private String geneCountDirectoryPath;
    private CompositeFilter geneFilters;

    public GeneExpressionDataLoad(String geneCountDirectoryPath) {
        this.geneCountDirectoryPath = geneCountDirectoryPath;
        this.geneFilters = new CompositeFilter();
    }

    @Override
    public GeneExpressionData getGeneExpressionFormattedData() {
        String geneExpressionFile = geneCountDirectoryPath + "\\data.txt";
        String[] geneExpressionFileLines = this.getFileLines(geneExpressionFile);

        String geneMetadataFile = geneCountDirectoryPath + "\\metadata.txt";
        String[] geneMetadataFileLines = this.getFileLines(geneMetadataFile);

        geneFilters.addfilter(new GeneFilterByTotalExpression(1000));
        geneFilters.addfilter(new GeneFilterByVariance(100));

        int numberOfGenes = getNumberOfFilteredGenes(geneExpressionFileLines);
        int numberOfComponents = geneMetadataFileLines.length - 1;

        // Read gene expression
        double[][] expressionData = new double[numberOfGenes][numberOfComponents];
        String[] geneIds = new String[numberOfGenes];
        int currentGene = 0;
        for (int row = 1; row < geneExpressionFileLines.length; row++) {
            String[] dataRow = this.getSplittedDataRow(geneExpressionFileLines[row], "\t");

            if (geneFilters.filterGene(dataRow)) {
                geneIds[currentGene] = dataRow[0];
                for (int c = 1; c <= numberOfComponents; c++) {
                    expressionData[currentGene][c - 1] = Double.parseDouble(dataRow[c]);
                }

                currentGene++;
            }
        }

        // Read metada
        String[] columnData = geneMetadataFileLines[0].split(",");
        HashMap<String, SampleMetadata> metadata = new HashMap<>();

        int numberOfReplicates = 0;
        int numberOfTimeSeries = 0;

        for (int row = 1; row < geneMetadataFileLines.length; row++) {
            String[] dataRow = this.getSplittedDataRow(geneMetadataFileLines[row], ",");
            int replicate = Integer.parseInt(dataRow[4]);
            int time = Integer.parseInt(dataRow[3]);

            metadata.put(dataRow[0], new SampleMetadata(dataRow[0], replicate, time));

            numberOfReplicates = Math.max(replicate, numberOfReplicates);
            numberOfTimeSeries = Math.max(replicate, numberOfTimeSeries);
        }

        return new GeneExpressionData(numberOfGenes, numberOfReplicates, numberOfTimeSeries, this.normalizeExpressionData(expressionData), metadata, geneIds, columnData);
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
            if (geneFilters.filterGene(this.getSplittedDataRow(geneExpressionFileLines[r], "\t"))) {
                numberOfFilteredGenes++;
            }
        }

        return numberOfFilteredGenes;
    }

    private String[] getSplittedDataRow(String dataRow, String splitChar) {
        return dataRow.split(splitChar);
    }
}
