package ClusterWorkflow;

import Common.GeneClusterData;
import Common.GeneExpressionData;
import Common.SampleMetadata;
import Common.WorkflowResult;
import Interfaces.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TracGLMWorkflow implements ClusterWorkflow {

    private static final double SIGNIFICANCE_THRESHOLD = 0.05;

    private final IDataLoad dataLoad;
    private final IGeneFilter independentFilter;
    private final ISampleFilter sampleFilter;
    private final IDataNormalizer baseNormalizer;
    private final IModelFitter glmProcessor;
    private final ISignificanceTester waldTester;
    private final IMultipleTestingCorrection fdrAdjuster;
    private final IClusteringAlgorithm clusterAlgo;

    public TracGLMWorkflow(IDataLoad dataLoad,
                           IGeneFilter independentFilter,
                           ISampleFilter sampleFilter,
                           IDataNormalizer baseNormalizer,
                           IModelFitter glmProcessor,
                           ISignificanceTester waldTester,
                           IMultipleTestingCorrection fdrAdjuster,
                           IClusteringAlgorithm clusterAlgo) {
        this.dataLoad = dataLoad;
        this.independentFilter = independentFilter;
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

        PreparedData prep = prepareData(rawData);

        FilteredData filtered = applyIndependentFilter(prep, rawData.getGeneIds());

        double[][] normalizedData = baseNormalizer.normalize(
                filtered.matrix, prep.replicatesPerTime, prep.sampleTimeMap, rawData.getNumberOfTimeSeries());

        Common.GLMFitResult glmResult = glmProcessor.fitModel(
                normalizedData, prep.replicatesPerTime, prep.sampleTimeMap, rawData.getNumberOfTimeSeries());

        double[][] rawPValues = waldTester.calculateRawPValues(glmResult);

        double[][] adjPValues = fdrAdjuster.adjustPValues(rawPValues);

        double[][] significantBetas = filterBetasBySignificance(glmResult.betas(), adjPValues, SIGNIFICANCE_THRESHOLD);
        String[] significantGeneIds = filterGeneIdsBySignificance(filtered.geneIds, adjPValues, SIGNIFICANCE_THRESHOLD);

        if (significantBetas.length == 0) {
            throw new RuntimeException("No genes passed the significance threshold (FDR < " + SIGNIFICANCE_THRESHOLD + ").");
        }

        // Strip the intercept (index 0) from the significant betas
        int dynamicTimePoints = rawData.getNumberOfTimeSeries() - 1;
        double[][] dynamicBetas = new double[significantBetas.length][dynamicTimePoints];
        for (int i = 0; i < significantBetas.length; i++) {
            System.arraycopy(significantBetas[i], 1, dynamicBetas[i], 0, dynamicTimePoints);
        }

        // Shift time labels and maps to match the stripped intercept
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
                significantGeneIds,
                shiftedTimeLabels,
                rawData.getMetadata(),
                resultReplicatesPerTime,
                resultSampleTimeMap,
                shiftedTimeLabels
        );

        GeneClusterData clusters = clusterAlgo.clusterGenes(finalDataForClustering);

        return new WorkflowResult(finalDataForClustering, clusters);
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

    private FilteredData applyIndependentFilter(PreparedData prep, String[] rawGeneIds) {
        List<Integer> validGeneIndices = new ArrayList<>();
        for (int i = 0; i < prep.matrix.length; i++) {
            if (independentFilter.filterGene(prep.matrix[i])) {
                validGeneIndices.add(i);
            }
        }

        if (validGeneIndices.isEmpty()) {
            throw new RuntimeException("No gene passed the independent filter");
        }

        int numValidGenes = validGeneIndices.size();
        double[][] filteredMatrix = new double[numValidGenes][prep.matrix[0].length];
        String[] filteredGeneIds = new String[numValidGenes];

        for (int i = 0; i < numValidGenes; i++) {
            int originalIndex = validGeneIndices.get(i);
            filteredMatrix[i] = prep.matrix[originalIndex];
            filteredGeneIds[i] = rawGeneIds[originalIndex];
        }

        return new FilteredData(filteredMatrix, filteredGeneIds);
    }

    private double[][] filterBetasBySignificance(double[][] betas, double[][] adjPValues, double threshold) {
        List<double[]> sigBetas = new ArrayList<>();
        for (int i = 0; i < betas.length; i++) {
            if (isSignificant(adjPValues[i], threshold)) {
                sigBetas.add(betas[i]);
            }
        }
        return sigBetas.toArray(new double[0][]);
    }

    private String[] filterGeneIdsBySignificance(String[] geneIds, double[][] adjPValues, double threshold) {
        List<String> sigIds = new ArrayList<>();
        for (int i = 0; i < geneIds.length; i++) {
            if (isSignificant(adjPValues[i], threshold)) {
                sigIds.add(geneIds[i]);
            }
        }
        return sigIds.toArray(new String[0]);
    }

    private boolean isSignificant(double[] pValues, double threshold) {
        for (double p : pValues) {
            if (p < threshold) {
                return true;
            }
        }
        return false;
    }

    private record PreparedData(double[][] matrix, int[] replicatesPerTime, int[] sampleTimeMap) {}
    private record FilteredData(double[][] matrix, String[] geneIds) {}
}