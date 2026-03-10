package ClusterWorkflow;

import Common.GeneClusterData;
import Common.GeneExpressionData;
import Common.WorkflowResult;
import Interfaces.ClusterWorkflow;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IDataLoad;
import Interfaces.IDataProcessor;

/**
 * Standard implementation of the clustering workflow that encapsulates
 * the core data loading, processing, and clustering logic.
 */
public class StandardClusterWorkflow implements ClusterWorkflow {
    private final IDataLoad dataLoad;
    private final IDataProcessor dataProcessor;
    private final IClusteringAlgorithm algorithm;

    public StandardClusterWorkflow(IDataLoad dataLoad, IDataProcessor dataProcessor, IClusteringAlgorithm algorithm) {
        this.dataLoad = dataLoad;
        this.dataProcessor = dataProcessor;
        this.algorithm = algorithm;
    }

    @Override
    public WorkflowResult execute() {
        GeneExpressionData rawData = dataLoad.getGeneExpressionFormattedData();
        GeneExpressionData processedData = dataProcessor.processData(rawData);
        GeneClusterData result = algorithm.clusterGenes(processedData);
        
        return new WorkflowResult(processedData, result);
    }
}
