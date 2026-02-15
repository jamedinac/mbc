package FileDataOperations;

import Common.FileFormat;
import Common.GeneExpressionData;
import Common.SampleMetadata;
import Filter.CompositeFilter;
import Filter.SampleFilter;
import Interfaces.IDataNormalizer;
import Interfaces.IGeneExpressionDataLoad;
import Interfaces.IGeneFilter;
import Normalizers.CompositeNormalizer;
import Utilities.FileUtilities;

import java.io.File;
import java.util.HashMap;


public class GeneExpressionDataLoad implements IGeneExpressionDataLoad {

    private final String geneCountDirectoryPath;
    private final String geneExpressionFileName;
    private final String metadataFileName;
    private final String replicateColumn;
    private final String timeSeriesColumn;
    private final String sampleIdColumn;

    private final int numberOfReplicates;
    private final int numberOfTimeSeries;

    private final CompositeFilter geneFilters;
    private final SampleFilter sampleFilter;
    private final CompositeNormalizer compositeNormalizer;

    FileFormat geneFileFormat;
    FileFormat metadataFileFormat;

    public GeneExpressionDataLoad(String geneCountDirectoryPath, String geneExpressionFileName, String metadataFileName, String replicateColumn, String timeSeriesColumn, String sampleIdColumn, int numberOfReplicates, int numberOfTimeSeries, FileFormat geneFileFormat, FileFormat metadataFileFormat) {
        this.geneCountDirectoryPath = geneCountDirectoryPath;
        this.geneExpressionFileName = geneExpressionFileName;
        this.metadataFileName = metadataFileName;
        this.replicateColumn = replicateColumn;
        this.timeSeriesColumn = timeSeriesColumn;
        this.sampleIdColumn = sampleIdColumn;

        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;

        this.geneFileFormat = geneFileFormat;
        this.metadataFileFormat = metadataFileFormat;

        this.sampleFilter = new SampleFilter();
        this.geneFilters = new CompositeFilter();
        this.compositeNormalizer = new CompositeNormalizer();
    }

    @Override
    public GeneExpressionData getGeneExpressionFormattedData() {
        /// Read metada
        String geneMetadataFile = geneCountDirectoryPath + File.separator + this.metadataFileName;
        String[] geneMetadataFileLines = FileUtilities.getFileLines(geneMetadataFile);

        String[] metadataColumnNames = FileUtilities.getSplitDataRow(geneMetadataFileLines[0], metadataFileFormat.getDelimiter());
        HashMap<String, SampleMetadata> metadata = this.getMetadata(metadataColumnNames, geneMetadataFileLines);

        /// Read gene expression
        String geneExpressionFile = geneCountDirectoryPath + File.separator + this.geneExpressionFileName;
        String[] geneExpressionFileLines = FileUtilities.getFileLines(geneExpressionFile);

        int numberOfComponents = this.getNumberOfComponents(geneMetadataFileLines, metadata);
        String[] geneDataColumnNames = this.getGeneDataColumnNames(FileUtilities.getSplitDataRow(geneExpressionFileLines[0], this.geneFileFormat.getDelimiter()), numberOfComponents, metadata);


        double[][] sampleFilteredExpressionData = this.getSampleFilteredExpressionData(geneExpressionFileLines, numberOfComponents, metadata);
        boolean[] filteredGenes = this.buildFilteredGene(sampleFilteredExpressionData);
        double [][] sortedData = this.getSortedData(sampleFilteredExpressionData, metadata, filteredGenes, geneDataColumnNames);
        double[][] normalizedData = this.compositeNormalizer.normalize(sortedData);

        int numberOfGenes = this.getNumberOfFilteredGenes(filteredGenes);
        String[] geneIds = this.getGeneIds(geneExpressionFileLines, filteredGenes, numberOfGenes);

        return new GeneExpressionData(numberOfGenes, numberOfReplicates, numberOfTimeSeries, normalizedData, geneIds);
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

    private HashMap<String, SampleMetadata> getMetadata(String[] metadataColumnNames, String[] geneMetadataFileLines) {
        HashMap<String, SampleMetadata> metadata = new HashMap<>();

        int sampleIdColumnIndex = this.getColumnIndex(sampleIdColumn, metadataColumnNames);
        int replicateColumnIndex = this.getColumnIndex(replicateColumn, metadataColumnNames);
        int timeSeriesColumnIndex = this.getColumnIndex(timeSeriesColumn, metadataColumnNames);

        for (int row = 1; row < geneMetadataFileLines.length; row++) {
            String[] dataRow = FileUtilities.getSplitDataRow(geneMetadataFileLines[row], metadataFileFormat.getDelimiter());
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

        return metadata;
    };

    private double[][] getSampleFilteredExpressionData(String[] geneExpressionFileLines, int numberOfComponents, HashMap<String, SampleMetadata> metadata) {
        String[] sampleIds = FileUtilities.getSplitDataRow(geneExpressionFileLines[0], this.geneFileFormat.getDelimiter());
        int rows = geneExpressionFileLines.length - 1;
        double[][] rawData = new double[rows][numberOfComponents];

        for (int row = 1; row <= rows; row++) {
            String[] splitRow = FileUtilities.getSplitDataRow(geneExpressionFileLines[row], geneFileFormat.getDelimiter());

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

    private double[][] getSortedData(double[][] data, HashMap<String, SampleMetadata> metadata, boolean[] filteredGenes, String[] geneColumn) {
        int numberOfGenes = this.getNumberOfFilteredGenes(filteredGenes);
        int numberOfComponents = this.numberOfReplicates * numberOfTimeSeries;
        double[][] sortedData = new double[numberOfGenes][numberOfComponents];

        for (int c=0; c < geneColumn.length; c++) {
            int replicate = metadata.get(geneColumn[c]).getReplicate();
            int time = metadata.get(geneColumn[c]).getTime();

            int currentGene = 0;
            for (int g=0; g < data.length; ++g) {
                if (filteredGenes[g]) {
                    sortedData[currentGene][time*numberOfReplicates + replicate - 1] = data[g][c];
                    currentGene++;
                }
            }
        }

        return sortedData;
    }

    private String[] getGeneDataColumnNames(String[] geneColumnNames, int numberOfComponents,  HashMap<String, SampleMetadata> metadata) {
        String[] geneDataColumnNames = new String[numberOfComponents];

        int currentColumn = 0;
        for (String column :  geneColumnNames) {
            if (this.sampleFilter.isValidSample(metadata.get(column))) {
                geneDataColumnNames[currentColumn] = column;
                currentColumn++;
            }
        }

        return geneDataColumnNames;
    }
    private String[] getGeneIds(String[] geneExpressionFileLines, boolean[] filteredGenes, int numberOfGenes) {
        String[] geneIds = new String[numberOfGenes];
        int currentGene = 0;

        for (int r=1; r<geneExpressionFileLines.length; r++) {
            if (filteredGenes[r-1]) {
                geneIds[currentGene] = FileUtilities.getSplitDataRow(geneExpressionFileLines[r], this.geneFileFormat.getDelimiter())[0];
                currentGene++;
            }
        }

        return geneIds;
    }

    private boolean[] buildFilteredGene(double[][] data) {
        int numberOfRows =  data.length;
        boolean[] filteredGenes = new boolean[numberOfRows];

        for (int r=0; r<numberOfRows; r++) {
            filteredGenes[r] = this.geneFilters.filterGene(data[r]);
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
            String[] metaDataRow = FileUtilities.getSplitDataRow(metadataFileLines[r], metadataFileFormat.getDelimiter());
            String sampleId = metaDataRow[0];

            if (this.sampleFilter.isValidSample(metadata.get(sampleId))) {
                numberOfComponents++;
            }
        }

        return numberOfComponents;
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
