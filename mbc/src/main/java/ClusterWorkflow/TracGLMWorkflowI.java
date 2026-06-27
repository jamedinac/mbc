package ClusterWorkflow;

import Common.GeneClusterData;
import Common.GeneExpressionData;
import Common.InputSummary;
import Common.SampleMetadata;
import Common.WorkflowResult;
import Enum.FilterStatus;
import Interfaces.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Significance-driven clustering workflow utilizing Generalized Linear Models (GLM).
 */
public class TracGLMWorkflowI implements IClusterWorkflow {

    private final IDataLoad dataLoad;
    private final IGeneFilter preNormalizationFilter;
    private final IGeneFilter postNormalizationFilter;
    private final ISampleFilter sampleFilter;
    private final IDataNormalizer baseNormalizer;
    private final IModelFitter glmProcessor;
    private final ISignificanceTester waldTester;
    private final IMultipleTestingCorrection fdrAdjuster;
    private final IClusteringAlgorithm clusterAlgo;

    public TracGLMWorkflowI(IDataLoad dataLoad,
                            IGeneFilter preNormalizationFilter,
                            IGeneFilter postNormalizationFilter,
                            ISampleFilter sampleFilter,
                            IDataNormalizer baseNormalizer,
                            IModelFitter glmProcessor,
                            ISignificanceTester waldTester,
                            IMultipleTestingCorrection fdrAdjuster,
                            IClusteringAlgorithm clusterAlgo) {
        this.dataLoad = dataLoad;
        this.preNormalizationFilter = preNormalizationFilter;
        this.postNormalizationFilter = postNormalizationFilter;
        this.sampleFilter = sampleFilter;
        this.baseNormalizer = baseNormalizer;
        this.glmProcessor = glmProcessor;
        this.waldTester = waldTester;
        this.fdrAdjuster = fdrAdjuster;
        this.clusterAlgo = clusterAlgo;
    }

    @Override
    public WorkflowResult execute() {
        GeneExpressionData rawData = dataLoad.getGeneExpressionFormattedData();
        InputSummary inputSummary = new InputSummary(rawData.getNumberOfGenes(), rawData.getSampleIds().length);

        // 1. Prepare Data (Sample Filtering & Sorting by Time)
        PreparedData prep = prepareData(rawData);

        Map<String, FilterStatus> filteredOutGenes = new LinkedHashMap<>();

        // 2. Pre-Normalization Filtering (e.g., Variance, Non-Zero)
        FilteredData preFiltered = applyFilter(prep.matrix(), prep.matrix(), rawData.getGeneIds(), preNormalizationFilter, filteredOutGenes);

        // 3. Base Normalization
        double[][] normalizedData = baseNormalizer.normalize(
                preFiltered.matrix(), prep.replicatesPerTime(), prep.sampleTimeMap(), rawData.getNumberOfTimeSeries());

        // 4. Model Fitting (GLM)
        Common.GLMFitResult glmResult = glmProcessor.fitModel(
                normalizedData, prep.replicatesPerTime(), prep.sampleTimeMap(), rawData.getNumberOfTimeSeries());

        // 5. Extract Statistical Significance (Wald Test)
        double[][] rawPValues = waldTester.calculateRawPValues(glmResult);

        // 6. Global Multiple Testing Correction (FDR)
        double[][] adjPValues = fdrAdjuster.adjustPValues(rawPValues);

        // 7. Post-Normalization Filtering (e.g., Significance)
        // We reuse the IGeneFilter interface to evaluate the p-value trajectories
        FilteredData postFiltered = applyFilter(adjPValues, glmResult.betas(), preFiltered.geneIds(), postNormalizationFilter, filteredOutGenes);

        double[][] finalBetasMatrix = postFiltered.matrix();
        String[] finalGeneIds = postFiltered.geneIds();

        // 8. Intercept Removal (Clustering on Dynamic Fold Changes only)
        int dynamicTimePoints = rawData.getNumberOfTimeSeries() - 1;
        double[][] dynamicBetas = new double[finalBetasMatrix.length][dynamicTimePoints];
        for (int i = 0; i < finalBetasMatrix.length; i++) {
            System.arraycopy(finalBetasMatrix[i], 1, dynamicBetas[i], 0, dynamicTimePoints);
        }

        // 9. Label Shifting
        String[] originalTimeLabels = rawData.getTimeLabels();
        String[] shiftedTimeLabels = new String[dynamicTimePoints];
        System.arraycopy(originalTimeLabels, 1, shiftedTimeLabels, 0, dynamicTimePoints);

        int[] resultReplicatesPerTime = new int[dynamicTimePoints];
        int[] resultSampleTimeMap = new int[dynamicTimePoints];
        for (int t = 0; t < dynamicTimePoints; t++) {
            resultReplicatesPerTime[t] = 1;
            resultSampleTimeMap[t] = t;
        }

        GeneExpressionData finalDataForClustering = new GeneExpressionData(
                dynamicBetas.length,
                dynamicBetas,
                finalGeneIds,
                shiftedTimeLabels,
                rawData.getMetadata(),
                resultReplicatesPerTime,
                resultSampleTimeMap,
                shiftedTimeLabels
        );

        // 10. Clustering
        GeneClusterData clusters = clusterAlgo.clusterGenes(finalDataForClustering);

        return new WorkflowResult(inputSummary, finalDataForClustering, clusters, filteredOutGenes);
    }

    private PreparedData prepareData(GeneExpressionData rawData) {
        double[][] rawMatrix = rawData.getExpressionData();
        String[] rawSampleIds = rawData.getSampleIds();
        HashMap<String, SampleMetadata> metadataMap = rawData.getMetadata();
        int numberOfTimeSeries = rawData.getNumberOfTimeSeries();

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

        double[][] sortedMatrix = new double[rawMatrix.length][totalSortedColumns];
        int[] sampleTimeMap = new int[totalSortedColumns];
        int[] currentReplicateIndexCounter = new int[numberOfTimeSeries];

        for (int originalIdx : validSampleIndices) {
            SampleMetadata metadata = metadataMap.get(rawSampleIds[originalIdx]);
            int t = metadata.getTime();
            int targetIndex = timeStartOffsets[t] + currentReplicateIndexCounter[t];

            for (int i = 0; i < rawMatrix.length; i++) {
                sortedMatrix[i][targetIndex] = rawMatrix[i][originalIdx];
            }
            sampleTimeMap[targetIndex] = t;
            currentReplicateIndexCounter[t]++;
        }

        return new PreparedData(sortedMatrix, filteredReplicatesPerTime, sampleTimeMap);
    }

    private FilteredData applyFilter(double[][] filterMatrix, double[][] keepMatrix, String[] geneIds, 
                                     IGeneFilter filter, Map<String, FilterStatus> filteredOutGenes) {
        List<Integer> validGeneIndices = new ArrayList<>();
        for (int i = 0; i < filterMatrix.length; i++) {
            FilterStatus status = filter.filterGene(filterMatrix[i]);
            if (status == FilterStatus.NOT_FILTERED) {
                validGeneIndices.add(i);
            } else {
                if (filteredOutGenes != null) {
                    filteredOutGenes.put(geneIds[i], status);
                }
            }
        }

        if (validGeneIndices.isEmpty()) {
            throw new RuntimeException("No genes passed the filter.");
        }

        int numValidGenes = validGeneIndices.size();
        double[][] filteredMatrix = new double[numValidGenes][keepMatrix[0].length];
        String[] filteredGeneIds = new String[numValidGenes];

        for (int i = 0; i < numValidGenes; i++) {
            int originalIndex = validGeneIndices.get(i);
            filteredMatrix[i] = keepMatrix[originalIndex];
            filteredGeneIds[i] = geneIds[originalIndex];
        }

        return new FilteredData(filteredMatrix, filteredGeneIds);
    }

    private record PreparedData(double[][] matrix, int[] replicatesPerTime, int[] sampleTimeMap) {}
    private record FilteredData(double[][] matrix, String[] geneIds) {}
}
