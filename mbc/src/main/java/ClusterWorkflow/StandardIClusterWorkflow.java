package ClusterWorkflow;

import Common.GeneClusterData;
import Common.GeneExpressionData;
import Common.InputSummary;
import Common.WorkflowResult;
import Interfaces.IClusterWorkflow;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IDataLoad;
import Interfaces.IDataProcessor;

import java.util.LinkedHashMap;

/**
 * Standard implementation of the clustering workflow that encapsulates
 * the core data loading, processing, and clustering logic.
 */
public class StandardIClusterWorkflow implements IClusterWorkflow {
    private final IDataLoad dataLoad;
    private final IDataProcessor dataProcessor;
    private final IClusteringAlgorithm algorithm;

    public StandardIClusterWorkflow(IDataLoad dataLoad, IDataProcessor dataProcessor, IClusteringAlgorithm algorithm) {
        this.dataLoad = dataLoad;
        this.dataProcessor = dataProcessor;
        this.algorithm = algorithm;
    }

    @Override
    public WorkflowResult execute() {
        GeneExpressionData rawData = dataLoad.getGeneExpressionFormattedData();
        InputSummary inputSummary = new InputSummary(rawData.getNumberOfGenes(), rawData.getSampleIds().length);
        GeneExpressionData processedData = dataProcessor.processData(rawData);
        GeneClusterData result = algorithm.clusterGenes(processedData);
        
        return new WorkflowResult(inputSummary, processedData, result, new LinkedHashMap<>());
    }
}
