package Common;

/**
 * Data Transfer Object that encapsulates the results of a clustering workflow,
 * including both the intermediate processed data and the final cluster assignments.
 */
public class WorkflowResult {
    private final InputSummary inputSummary;
    private final GeneExpressionData processedData;
    private final GeneClusterData clusterData;

    public WorkflowResult(InputSummary inputSummary, GeneExpressionData processedData, GeneClusterData clusterData) {
        this.inputSummary = inputSummary;
        this.processedData = processedData;
        this.clusterData = clusterData;
    }

    public InputSummary getInputSummary() {
        return inputSummary;
    }

    public GeneExpressionData getProcessedData() {
        return processedData;
    }

    public GeneClusterData getClusterData() {
        return clusterData;
    }
}
