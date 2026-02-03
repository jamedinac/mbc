package GeneExpressionDataOperation;

import Common.GeneExpressionData;
import Common.SampleMetadata;
import Filter.CompositeFilter;
import Filter.SampleFilter;
import Interfaces.IGeneExpressionDataSource;
import Interfaces.IGeneFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;


public class GeneExpressionDataLoad implements IGeneExpressionDataSource {

    private final String geneCountDirectoryPath;
    private final int replicateColumnIndex;
    private final int timeSeriesColumnIndex;
    private final CompositeFilter geneFilters;
    private final SampleFilter sampleFilter;

    private final int geneIdColumnIndex = 0;
    private final int sampleIdColumnIndex = 0;

    public GeneExpressionDataLoad(String geneCountDirectoryPath, int replicateColumnIndex, int timeSeriesColumnIndex) {
        this.geneCountDirectoryPath = geneCountDirectoryPath;
        this.geneFilters = new CompositeFilter();
        this.replicateColumnIndex = replicateColumnIndex;
        this.timeSeriesColumnIndex = timeSeriesColumnIndex;
        this.sampleFilter = new SampleFilter();
    }

    @Override
    public GeneExpressionData getGeneExpressionFormattedData() {
        String geneExpressionFile = geneCountDirectoryPath + "\\data.txt";
        String[] geneExpressionFileLines = this.getFileLines(geneExpressionFile);

        String geneMetadataFile = geneCountDirectoryPath + "\\metadata.txt";
        String[] geneMetadataFileLines = this.getFileLines(geneMetadataFile);

        /// Read metada
        String[] metadataColumnNames = this.getSplitDataRow(geneMetadataFileLines[0],",");
        HashMap<String, SampleMetadata> metadata = new HashMap<>();

        int numberOfReplicates = 0;
        int numberOfTimeSeries = 0;

        for (int row = 1; row < geneMetadataFileLines.length; row++) {
            String[] dataRow = this.getSplitDataRow(geneMetadataFileLines[row], ",");
            String sampleId = dataRow[sampleIdColumnIndex];
            int replicate = Integer.parseInt(dataRow[replicateColumnIndex]);
            int time = Integer.parseInt(dataRow[timeSeriesColumnIndex]);

            metadata.put(sampleId, new SampleMetadata(sampleId, replicate, time));

            for (int c = 1; c < dataRow.length; c++) {
                if (c == replicateColumnIndex) {
                    replicate = Integer.parseInt(dataRow[c]);
                } else if (c == timeSeriesColumnIndex) {
                    time = Integer.parseInt(dataRow[c]);
                } else {
                    SampleMetadata sampleIdMetadata = metadata.get(sampleId);
                    sampleIdMetadata.addMetadataKey(metadataColumnNames[c], dataRow[c]);

                    metadata.put(sampleId, sampleIdMetadata);
                }
            }

            numberOfReplicates = Math.max(replicate, numberOfReplicates);
            numberOfTimeSeries = Math.max(time, numberOfTimeSeries);
        }

        int numberOfGenes = getNumberOfFilteredGenes(geneExpressionFileLines);
        int numberOfComponents = getNumberOfComponents(geneMetadataFileLines, metadata);

        /// Read gene expression
        double[][] expressionData = new double[numberOfGenes][numberOfComponents];
        String[] geneIds = new String[numberOfGenes];
        String[] geneDataColumnNames = this.getSplitDataRow(geneExpressionFileLines[0], "\t");

        int currentGene = 0;
        for (int row = 1; row < geneExpressionFileLines.length; row++) {
            String[] dataRow = this.getSplitDataRow(geneExpressionFileLines[row], "\t");

            if (geneFilters.filterGene(dataRow)) {
                geneIds[currentGene] = dataRow[geneIdColumnIndex];

                int currentComponent = 0;
                for (int c = 1; c < dataRow.length; c++) {
                    String sampleId = geneDataColumnNames[c];

                    if (metadata.containsKey(sampleId) && this.sampleFilter.isValidSample(metadata.get(sampleId))) {
                        expressionData[currentGene][currentComponent] = Double.parseDouble(dataRow[c]);
                        currentComponent++;
                    }
                }

                currentGene++;
            }
        }

        return new GeneExpressionData(numberOfGenes, numberOfReplicates, numberOfTimeSeries, this.normalizeExpressionData(expressionData), metadata, geneIds, metadataColumnNames);
    }

    @Override
    public void addGeneFilter(IGeneFilter filter) {
        this.geneFilters.addfilter(filter);
    }

    @Override
    public void addSampleFilter(String sampleTrait, String sampleTraitValue) {
        this.sampleFilter.addValidSampleTrait(sampleTrait, sampleTraitValue);
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
            if (geneFilters.filterGene(this.getSplitDataRow(geneExpressionFileLines[r], "\t"))) {
                numberOfFilteredGenes++;
            }
        }

        return numberOfFilteredGenes;
    }

    private int getNumberOfComponents(String[] metadataFileLines, HashMap<String, SampleMetadata> metadata) {
        int numberOfRows = metadataFileLines.length;
        int numberOfComponents = 0;

        for (int r = 1; r < numberOfRows; r++) {
            String[] metaDataRow = this.getSplitDataRow(metadataFileLines[r], ",");
            String sampleId = metaDataRow[0];

            if (this.sampleFilter.isValidSample(metadata.get(sampleId))) {
                numberOfComponents++;
            }
        }

        return numberOfComponents;
    }

    private String[] getSplitDataRow(String dataRow, String splitChar) {
        return dataRow.split(splitChar);
    }
}
