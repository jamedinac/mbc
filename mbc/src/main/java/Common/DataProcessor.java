package Common;

import Interfaces.IDataProcessor;
import Interfaces.IDataNormalizer;
import Interfaces.IGeneFilter;
import Interfaces.ISampleFilter;
import Interfaces.IReplicateCompression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataProcessor implements IDataProcessor {
    private final IGeneFilter geneFilter;
    private final ISampleFilter sampleFilter;
    private final IReplicateCompression compression;
    private final IDataNormalizer normalizer;

    public DataProcessor(IGeneFilter geneFilter, ISampleFilter sampleFilter, IReplicateCompression compression, IDataNormalizer normalizer) {
        this.geneFilter = geneFilter;
        this.sampleFilter = sampleFilter;
        this.compression = compression;
        this.normalizer = normalizer;
    }

    public GeneExpressionData processData(GeneExpressionData rawData) {
        double[][] rawMatrix = rawData.getExpressionData();
        String[] rawGeneIds = rawData.getGeneIds();
        String[] rawSampleIds = rawData.getSampleIds();
        HashMap<String, SampleMetadata> metadataMap = rawData.getMetadata();
        int numberOfTimeSeries = rawData.getNumberOfTimeSeries();

        // 1. Identify Valid Samples and Compute Dynamic Offsets
        List<Integer> validSampleIndices = new ArrayList<>();
        int[] filteredReplicatesPerTime = new int[numberOfTimeSeries];
        
        for (int j = 0; j < rawSampleIds.length; j++) {
            SampleMetadata metadata = metadataMap.get(rawSampleIds[j]);
            if (sampleFilter.isValidSample(metadata)) {
                validSampleIndices.add(j);
                filteredReplicatesPerTime[metadata.getTime()]++;
            }
        }

        int[] timeStartOffsets = new int[numberOfTimeSeries];
        int totalSortedColumns = 0;
        for (int t = 0; t < numberOfTimeSeries; t++) {
            timeStartOffsets[t] = totalSortedColumns;
            totalSortedColumns += filteredReplicatesPerTime[t];
        }

        // Populate Sorted and Sample Filtered Matrix using Cumulative Offsets
        double[][] sortedAndSampleFilteredMatrix = new double[rawMatrix.length][totalSortedColumns];
        int[] sampleTimeMap = new int[totalSortedColumns];
        int[] currentReplicateIndexCounter = new int[numberOfTimeSeries];

        for (int originalIdx : validSampleIndices) {
            SampleMetadata metadata = metadataMap.get(rawSampleIds[originalIdx]);
            int t = metadata.getTime();
            int targetIndex = timeStartOffsets[t] + currentReplicateIndexCounter[t];
            
            for (int i = 0; i < rawMatrix.length; i++) {
                sortedAndSampleFilteredMatrix[i][targetIndex] = rawMatrix[i][originalIdx];
            }
            sampleTimeMap[targetIndex] = t;
            currentReplicateIndexCounter[t]++;
        }

        // 2. Filter Genes
        List<Integer> validGeneIndices = new ArrayList<>();
        for (int i = 0; i < rawMatrix.length; i++) {
            if (geneFilter.filterGene(sortedAndSampleFilteredMatrix[i])) {
                validGeneIndices.add(i);
            }
        }

        if (validGeneIndices.isEmpty()) {
            throw new RuntimeException("No gene passed the filter");
        }

        // 3. Build Filtered Matrix and Gene IDs
        int numValidGenes = validGeneIndices.size();
        double[][] filteredMatrix = new double[numValidGenes][totalSortedColumns];
        String[] filteredGeneIds = new String[numValidGenes];

        for (int i = 0; i < numValidGenes; i++) {
            int originalIndex = validGeneIndices.get(i);
            filteredMatrix[i] = sortedAndSampleFilteredMatrix[originalIndex];
            filteredGeneIds[i] = rawGeneIds[originalIndex];
        }

        // 4. Normalize
        double[][] normalizedData = normalizer.normalize(filteredMatrix, filteredReplicatesPerTime, sampleTimeMap, numberOfTimeSeries);

        // 5. Compress
        double[][] resultData;
        int[] resultReplicatesPerTime;
        int[] resultSampleTimeMap;

        if (normalizedData[0].length == numberOfTimeSeries) {
            // Normalizer already compressed the data (like IRLS)
            resultData = normalizedData;
            resultReplicatesPerTime = new int[numberOfTimeSeries];
            resultSampleTimeMap = new int[numberOfTimeSeries];
            for (int t = 0; t < numberOfTimeSeries; t++) {
                resultReplicatesPerTime[t] = 1;
                resultSampleTimeMap[t] = t;
            }
        } else {
            // Standard normalizer, still need to compress replicates
            resultData = compression.compress(normalizedData, filteredReplicatesPerTime, numberOfTimeSeries);
            resultReplicatesPerTime = new int[numberOfTimeSeries];
            resultSampleTimeMap = new int[numberOfTimeSeries];
            for (int t = 0; t < numberOfTimeSeries; t++) {
                resultReplicatesPerTime[t] = 1;
                resultSampleTimeMap[t] = t;
            }
        }

        // 6. Return Result - using the actual time labels from raw data as sample names for compressed data
        String[] timeLabels = rawData.getTimeLabels();
        return new GeneExpressionData(numValidGenes, resultData, filteredGeneIds, timeLabels, metadataMap, resultReplicatesPerTime, resultSampleTimeMap, timeLabels);
    }
}
