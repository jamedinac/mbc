package FileDataOperations;

import Enum.FileFormat;
import Common.GeneExpressionData;
import Common.SampleMetadata;
import Interfaces.IDataLoad;
import Utilities.FileUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataLoad implements IDataLoad {

    private final String geneExpressionFileName;
    private final String metadataFileName;
    private final String timeSeriesColumn;
    private final String sampleIdColumn;
    private String[] timeLabels;
    private int numberOfTimeSeries;

    public DataLoad(String geneExpressionFileName, String metadataFileName, String timeSeriesColumn, String sampleIdColumn) {
        this.geneExpressionFileName = geneExpressionFileName;
        this.metadataFileName = metadataFileName;
        this.timeSeriesColumn = timeSeriesColumn;
        this.sampleIdColumn = sampleIdColumn;
    }

    @Override
    public GeneExpressionData getGeneExpressionFormattedData() {
        //0. Detects file format
        FileFormat metadataFileFormat = FileUtilities.detectFormat(this.metadataFileName);
        FileFormat geneFileFormat = FileUtilities.detectFormat(this.geneExpressionFileName);

        //1. Read metadata (populates this.numberOfTimeSeries and this.timeLabels)
        String[] geneMetadataFileLines = FileUtilities.getFileLines(this.metadataFileName);
        String[] metadataColumnNames = FileUtilities.getSplitDataRow(geneMetadataFileLines[0], metadataFileFormat.getDelimiter());
        HashMap<String, SampleMetadata> metadata = this.getMetadata(metadataColumnNames, geneMetadataFileLines, metadataFileFormat);

        //2. Read gene expression
        String[] geneExpressionFileLines = FileUtilities.getFileLines(this.geneExpressionFileName);
        String[] header = FileUtilities.getSplitDataRow(geneExpressionFileLines[0], geneFileFormat.getDelimiter());
        
        int numberOfSamples = header.length - 1;
        String[] sampleIds = new String[numberOfSamples];
        System.arraycopy(header, 1, sampleIds, 0, numberOfSamples);

        // Dynamically compute replicates per time point and the sample to time map
        int[] replicatesPerTime = new int[this.numberOfTimeSeries];
        int[] sampleTimeMap = new int[numberOfSamples];
        for (int i = 0; i < numberOfSamples; i++) {
            SampleMetadata sm = metadata.get(sampleIds[i]);
            if (sm != null) {
                int denseTimeIndex = sm.getTime();
                replicatesPerTime[denseTimeIndex]++;
                sampleTimeMap[i] = denseTimeIndex;
            }
        }

        int numberOfGenes = geneExpressionFileLines.length - 1;
        String[] geneIds = new String[numberOfGenes];
        double[][] expressionData = new double[numberOfGenes][numberOfSamples];

        for (int i = 0; i < numberOfGenes; i++) {
            String[] row = FileUtilities.getSplitDataRow(geneExpressionFileLines[i + 1], geneFileFormat.getDelimiter());
            geneIds[i] = row[0];
            for (int j = 0; j < numberOfSamples; j++) {
                expressionData[i][j] = Double.parseDouble(row[j + 1]);
            }
        }

        return new GeneExpressionData(numberOfGenes, expressionData, geneIds, sampleIds, metadata, replicatesPerTime, sampleTimeMap, this.timeLabels);
    }

    private HashMap<String, SampleMetadata> getMetadata(String[] metadataColumnNames, String[] geneMetadataFileLines, FileFormat metadataFileFormat) {
        int sampleIdColumnIndex = this.getColumnIndex(sampleIdColumn, metadataColumnNames);
        int timeSeriesColumnIndex = this.getColumnIndex(timeSeriesColumn, metadataColumnNames);

        // Pass 1: Discover unique time points from metadata
        Set<Double> uniqueTimesSet = new HashSet<>();
        for (int row = 1; row < geneMetadataFileLines.length; row++) {
            String[] dataRow = FileUtilities.getSplitDataRow(geneMetadataFileLines[row], metadataFileFormat.getDelimiter());
            String rawTime = dataRow[timeSeriesColumnIndex];
            uniqueTimesSet.add(Double.parseDouble(rawTime)); // O(1) insertion
        }
        
        // Sort to maintain chronological order and build dense index map
        List<Double> sortedTimes = new ArrayList<>(uniqueTimesSet);
        Collections.sort(sortedTimes); // O(N log N) sorting only once
        
        this.numberOfTimeSeries = sortedTimes.size();
        this.timeLabels = new String[this.numberOfTimeSeries];
        Map<Double, Integer> timePointToIndex = new HashMap<>(); // O(1) lookups
        
        for (int i = 0; i < this.numberOfTimeSeries; i++) {
            Double t = sortedTimes.get(i);
            timePointToIndex.put(t, i);
            this.timeLabels[i] = String.valueOf(t);
        }

        // Pass 2: Assign metadata using the dense index
        HashMap<String, SampleMetadata> metadata = new HashMap<>();
        for (int row = 1; row < geneMetadataFileLines.length; row++) {
            String[] dataRow = FileUtilities.getSplitDataRow(geneMetadataFileLines[row], metadataFileFormat.getDelimiter());
            String sampleId = dataRow[sampleIdColumnIndex];

            // Map the raw biological time to our dense, isolated index (e.g., 10 -> 2)
            double rawBiologicalTime = Double.parseDouble(dataRow[timeSeriesColumnIndex]);
            int denseTimeIndex = timePointToIndex.get(rawBiologicalTime);

            SampleMetadata sampleMetadata = new SampleMetadata(sampleId, denseTimeIndex);
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
