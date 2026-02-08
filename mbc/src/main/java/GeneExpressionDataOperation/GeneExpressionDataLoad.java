package GeneExpressionDataOperation;

import Common.FileExtension;
import Common.GeneExpressionData;
import Common.SampleMetadata;
import Filter.CompositeFilter;
import Filter.SampleFilter;
import Interfaces.IDataNormalizer;
import Interfaces.IGeneExpressionDataLoad;
import Interfaces.IGeneFilter;
import Normalizers.CompositeNormalizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;


public class GeneExpressionDataLoad implements IGeneExpressionDataLoad {

    private final String geneCountDirectoryPath;
    private final String replicateColumn;
    private final String timeSeriesColumn;
    private final String sampleIdColumn;

    private final int numberOfReplicates;
    private final int numberOfTimeSeries;

    private final CompositeFilter geneFilters;
    private final SampleFilter sampleFilter;
    private final CompositeNormalizer compositeNormalizer;

    FileExtension geneFileExtension;
    FileExtension metadataFileExtension;

    public GeneExpressionDataLoad(String geneCountDirectoryPath, String replicateColumn, String timeSeriesColumn, String sampleIdColumn, int numberOfReplicates, int numberOfTimeSeries, FileExtension geneFileExtension, FileExtension metadataFileExtension) {
        this.geneCountDirectoryPath = geneCountDirectoryPath;
        this.replicateColumn = replicateColumn;
        this.timeSeriesColumn = timeSeriesColumn;
        this.sampleIdColumn = sampleIdColumn;

        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;

        this.geneFileExtension = geneFileExtension;
        this.metadataFileExtension = metadataFileExtension;

        this.sampleFilter = new SampleFilter();
        this.geneFilters = new CompositeFilter();
        this.compositeNormalizer = new CompositeNormalizer();
    }

    @Override
    public GeneExpressionData getGeneExpressionFormattedData() {
        /// Read metada
        String geneMetadataFile = geneCountDirectoryPath + "\\metadata.txt";
        String[] geneMetadataFileLines = this.getFileLines(geneMetadataFile);

        String[] metadataColumnNames = this.getSplitDataRow(geneMetadataFileLines[0],metadataFileExtension.getDelimiter());
        HashMap<String, SampleMetadata> metadata = new HashMap<>();

        int sampleIdColumnIndex = this.getColumnIndex(sampleIdColumn, metadataColumnNames);
        int replicateColumnIndex = this.getColumnIndex(replicateColumn, metadataColumnNames);
        int timeSeriesColumnIndex = this.getColumnIndex(timeSeriesColumn, metadataColumnNames);

        for (int row = 1; row < geneMetadataFileLines.length; row++) {
            String[] dataRow = this.getSplitDataRow(geneMetadataFileLines[row], metadataFileExtension.getDelimiter());
            String sampleId = dataRow[sampleIdColumnIndex];

            int replicate = Integer.parseInt(dataRow[replicateColumnIndex]);
            int time = Integer.parseInt(dataRow[timeSeriesColumnIndex]);

            metadata.put(sampleId, new SampleMetadata(sampleId, replicate, time));

            for (int c = 1; c < dataRow.length; c++) {
                SampleMetadata sampleIdMetadata = metadata.get(sampleId);
                sampleIdMetadata.addMetadataKey(metadataColumnNames[c], dataRow[c]);
                metadata.put(sampleId, sampleIdMetadata);
            }
        }

        /// Read gene expression
        String geneExpressionFile = geneCountDirectoryPath + "\\data.txt";
        String[] geneExpressionFileLines = this.getFileLines(geneExpressionFile);

        int numberOfComponents = this.getNumberOfComponents(geneMetadataFileLines, metadata);

        double[][] rawData = this.getRawExpressionData(geneExpressionFileLines, numberOfComponents, metadata);
        double[][] normalizedData = this.compositeNormalizer.normalize(rawData);
        boolean[] filteredGenes = this.buildFilteredGene(rawData);

        int numberOfGenes = this.getNumberOfFilteredGenes(filteredGenes);
        double[][] expressionData = new double[numberOfGenes][numberOfComponents];

        String[] geneIds = this.getGeneIds(geneExpressionFileLines, filteredGenes, numberOfGenes);
        String[] geneDataColumnNames = this.getSplitDataRow(geneExpressionFileLines[0], this.geneFileExtension.getDelimiter());

        int currentGene = 0;
        for (int r = 0; r < normalizedData.length; r++) {
            if (filteredGenes[r]) {
                expressionData[currentGene] = normalizedData[r];
                currentGene++;
            }
        }

        return new GeneExpressionData(numberOfGenes, numberOfReplicates, numberOfTimeSeries, expressionData, metadata, geneIds, geneDataColumnNames);
    }

    @Override
    public void addGeneFilter(IGeneFilter filter) {
        this.geneFilters.addfilter(filter);
    }

    @Override
    public void addSampleFilter(String sampleTrait, String sampleTraitValue) {
        this.sampleFilter.addValidSampleTrait(sampleTrait, sampleTraitValue);
    }

    @Override
    public void addNormalizer(IDataNormalizer normalizer) {
        this.compositeNormalizer.add(normalizer);
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

    private double[][] getRawExpressionData (String[] geneExpressionFileLines, int numberOfComponents, HashMap<String, SampleMetadata> metadata) {
        String[] sampleIds = this.getSplitDataRow(geneExpressionFileLines[0], this.geneFileExtension.getDelimiter());
        int rows = geneExpressionFileLines.length - 1;
        double[][] rawData = new double[rows][numberOfComponents];

        for (int row = 1; row <= rows; row++) {
            String[] splitRow = this.getSplitDataRow(geneExpressionFileLines[row], geneFileExtension.getDelimiter());

            int currentComponent = 0;
            for (int column = 1; column < splitRow.length; column++) {
                if (this.sampleFilter.isValidSample(metadata.get(sampleIds[column]))) {
                    rawData[row-1][currentComponent] = Double.parseDouble(splitRow[column]);
                    currentComponent++;
                }
            }
        }

        return rawData;
    }

    private String[] getGeneIds(String[] geneExpressionFileLines, boolean[] filteredGenes, int numberOfGenes) {
        String[] geneIds = new String[numberOfGenes];
        int currentGene = 0;

        for (int r=0; r<filteredGenes.length; r++) {
            if (filteredGenes[r]) {
                geneIds[currentGene] = this.getSplitDataRow(geneExpressionFileLines[r], this.geneFileExtension.getDelimiter())[0];
                currentGene++;
            }
        }

        return geneIds;
    }

    private boolean[] buildFilteredGene(double[][] normalizedData) {
        int numberOfRows =  normalizedData.length;
        boolean[] filteredGenes = new boolean[numberOfRows];

        for (int r=0; r<numberOfRows; r++) {
            filteredGenes[r] = this.geneFilters.filterGene(normalizedData[r]);
        }

        return filteredGenes;
    }

    private int getNumberOfFilteredGenes (boolean[] filteredGenes) {
        int numberOfFilteredGenes = 0;

        for (boolean filteredGene : filteredGenes) {
            if (filteredGene) {
                numberOfFilteredGenes++;
            }
        }

        if (numberOfFilteredGenes == 0) {
            throw new RuntimeException("No gene passed the filter");
        }

        return numberOfFilteredGenes;
    }

    private int getNumberOfComponents(String[] metadataFileLines, HashMap<String, SampleMetadata> metadata) {
        int numberOfRows = metadataFileLines.length;
        int numberOfComponents = 0;

        for (int r = 1; r < numberOfRows; r++) {
            String[] metaDataRow = this.getSplitDataRow(metadataFileLines[r], metadataFileExtension.getDelimiter());
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

    private int getColumnIndex(String column, String[] columnNames) {
        int index = -1;
        for (int i = 0; i < columnNames.length; i++) {
            if (column.equals(columnNames[i])) {
                index = i;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Column not found: " + column);
        }
        return index;
    }
}
