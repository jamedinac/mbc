package Common;

import Interfaces.IDataNormalizer;
import Interfaces.IGeneFilter;
import Interfaces.ISampleFilter;
import Interfaces.IReplicateCompression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataProcessor {
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
        int numberOfReplicates = rawData.getNumberOfReplicates();
        int numberOfTimeSeries = rawData.getNumberOfTimeSeries();

        // 1. Filter Samples and Identify Positions for Sorting
        // Position formula: time * numberOfReplicates + replicate - 1
        int sortedColumns = numberOfReplicates * numberOfTimeSeries;
        double[][] sortedAndSampleFilteredMatrix = new double[rawMatrix.length][sortedColumns];
        boolean[] validColumnsInSorted = new boolean[sortedColumns];

        for (int j = 0; j < rawSampleIds.length; j++) {
            String sampleId = rawSampleIds[j];
            SampleMetadata metadata = metadataMap.get(sampleId);

            if (sampleFilter.isValidSample(metadata)) {
                int replicate = metadata.getReplicate();
                int time = metadata.getTime();
                int targetIndex = time * numberOfReplicates + replicate - 1;

                if (targetIndex >= 0 && targetIndex < sortedColumns) {
                    for (int i = 0; i < rawMatrix.length; i++) {
                        sortedAndSampleFilteredMatrix[i][targetIndex] = rawMatrix[i][j];
                    }
                    validColumnsInSorted[targetIndex] = true;
                }
            }
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
        double[][] filteredMatrix = new double[numValidGenes][sortedColumns];
        String[] filteredGeneIds = new String[numValidGenes];

        for (int i = 0; i < numValidGenes; i++) {
            int originalIndex = validGeneIndices.get(i);
            filteredMatrix[i] = sortedAndSampleFilteredMatrix[originalIndex];
            filteredGeneIds[i] = rawGeneIds[originalIndex];
        }

        // 4. Compress
        double[][] compressedData = compression.compress(filteredMatrix, numberOfReplicates, numberOfTimeSeries);

        // 5. Normalize
        double[][] normalizedData = normalizer.normalize(compressedData);

        // 6. Return Result
        // For compressed data, sample labels could be "Time 0", "Time 1", etc.
        String[] timeLabels = new String[numberOfTimeSeries];
        for (int t = 0; t < numberOfTimeSeries; t++) {
            timeLabels[t] = "Time " + t;
        }

        return new GeneExpressionData(numValidGenes, normalizedData, filteredGeneIds, timeLabels, metadataMap, numberOfReplicates, numberOfTimeSeries);
    }
}
