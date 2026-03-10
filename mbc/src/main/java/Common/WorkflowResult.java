package Common;

/**
 * Data Transfer Object that encapsulates the results of a clustering workflow,
 * including both the intermediate processed data and the final cluster assignments.
 */
public class WorkflowResult {
    private final GeneExpressionData processedData;
    private final GeneClusterData clusterData;

    public WorkflowResult(GeneExpressionData processedData, GeneClusterData clusterData) {
        this.processedData = processedData;
        this.clusterData = clusterData;
    }

    public GeneExpressionData getProcessedData() {
        return processedData;
    }

    public GeneClusterData getClusterData() {
        return clusterData;
    }
}
