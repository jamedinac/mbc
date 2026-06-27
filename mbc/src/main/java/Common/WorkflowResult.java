package Common;

import Enum.FilterStatus;
import java.util.Map;

/**
 * Data Transfer Object that encapsulates the results of a clustering workflow,
 * including both the intermediate processed data and the final cluster assignments.
 */
public class WorkflowResult {
    private final InputSummary inputSummary;
    private final GeneExpressionData processedData;
    private final GeneClusterData clusterData;
    private final Map<String, FilterStatus> filteredOutGenes;

    public WorkflowResult(InputSummary inputSummary, GeneExpressionData processedData, 
                          GeneClusterData clusterData, Map<String, FilterStatus> filteredOutGenes) {
        this.inputSummary = inputSummary;
        this.processedData = processedData;
        this.clusterData = clusterData;
        this.filteredOutGenes = filteredOutGenes;
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

    public Map<String, FilterStatus> getFilteredOutGenes() {
        return filteredOutGenes;
    }
}
