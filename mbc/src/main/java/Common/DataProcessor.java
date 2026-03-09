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

        // 1. Identify Valid Samples and Compute Dynamic Offsets (Phase 2)
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

        // 4. Compress
        // Note: For Phase 2, we pass a placeholder for numberOfReplicates to maintain compilation.
        // This will be corrected in Phase 3 when signatures are updated.
        int placeholderReplicates = filteredReplicatesPerTime.length > 0 ? filteredReplicatesPerTime[0] : 0;
        double[][] compressedData = compression.compress(filteredMatrix, placeholderReplicates, numberOfTimeSeries);

        // 5. Normalize
        double[][] normalizedData = normalizer.normalize(compressedData);

        // 6. Return Result
        String[] timeLabels = new String[numberOfTimeSeries];
        int[] outReplicatesPerTime = new int[numberOfTimeSeries];
        int[] outSampleTimeMap = new int[numberOfTimeSeries];
        for (int t = 0; t < numberOfTimeSeries; t++) {
            timeLabels[t] = "Time " + t;
            outReplicatesPerTime[t] = 1;
            outSampleTimeMap[t] = t;
        }

        return new GeneExpressionData(numValidGenes, normalizedData, filteredGeneIds, timeLabels, metadataMap, outReplicatesPerTime, outSampleTimeMap, numberOfTimeSeries);
    }
}
