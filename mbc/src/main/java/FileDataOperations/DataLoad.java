package FileDataOperations;

import Enum.FileFormat;
import Common.GeneExpressionData;
import Common.SampleMetadata;
import Interfaces.IDataLoad;
import Utilities.FileUtilities;

import java.util.HashMap;

public class DataLoad implements IDataLoad {

    private final String geneExpressionFileName;
    private final String metadataFileName;
    private final String replicateColumn;
    private final String timeSeriesColumn;
    private final String sampleIdColumn;
    private final int numberOfReplicates;
    private final int numberOfTimeSeries;

    private final FileFormat geneFileFormat;
    private final FileFormat metadataFileFormat;

    public DataLoad(String geneExpressionFileName, String metadataFileName, String replicateColumn, String timeSeriesColumn, String sampleIdColumn, int numberOfReplicates, int numberOfTimeSeries, FileFormat geneFileFormat, FileFormat metadataFileFormat) {
        this.geneExpressionFileName = geneExpressionFileName;
        this.metadataFileName = metadataFileName;
        this.replicateColumn = replicateColumn;
        this.timeSeriesColumn = timeSeriesColumn;
        this.sampleIdColumn = sampleIdColumn;
        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;
        this.geneFileFormat = geneFileFormat;
        this.metadataFileFormat = metadataFileFormat;
    }

    @Override
    public GeneExpressionData getGeneExpressionFormattedData() {
        /// Read metadata
        String[] geneMetadataFileLines = FileUtilities.getFileLines(this.metadataFileName);
        String[] metadataColumnNames = FileUtilities.getSplitDataRow(geneMetadataFileLines[0], metadataFileFormat.getDelimiter());
        HashMap<String, SampleMetadata> metadata = this.getMetadata(metadataColumnNames, geneMetadataFileLines);

        /// Read gene expression
        String[] geneExpressionFileLines = FileUtilities.getFileLines(this.geneExpressionFileName);
        String[] header = FileUtilities.getSplitDataRow(geneExpressionFileLines[0], this.geneFileFormat.getDelimiter());
        
        int numberOfSamples = header.length - 1;
        String[] sampleIds = new String[numberOfSamples];
        System.arraycopy(header, 1, sampleIds, 0, numberOfSamples);

        int numberOfGenes = geneExpressionFileLines.length - 1;
        String[] geneIds = new String[numberOfGenes];
        double[][] expressionData = new double[numberOfGenes][numberOfSamples];

        for (int i = 0; i < numberOfGenes; i++) {
            String[] row = FileUtilities.getSplitDataRow(geneExpressionFileLines[i + 1], this.geneFileFormat.getDelimiter());
            geneIds[i] = row[0];
            for (int j = 0; j < numberOfSamples; j++) {
                expressionData[i][j] = Double.parseDouble(row[j + 1]);
            }
        }

        return new GeneExpressionData(numberOfGenes, expressionData, geneIds, sampleIds, metadata, numberOfReplicates, numberOfTimeSeries);
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

            SampleMetadata sampleMetadata = new SampleMetadata(sampleId, replicate, time);
            for (int c = 0; c < dataRow.length; c++) {
                sampleMetadata.addMetadataKey(metadataColumnNames[c], dataRow[c]);
            }
            metadata.put(sampleId, sampleMetadata);
        }

        return metadata;
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
