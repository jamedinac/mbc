package Interfaces;

import Common.WorkflowResult;

/**
 * Interface that defines the standard execution contract for a clustering workflow.
 */
public interface IClusterWorkflow {
    /**
     * Executes the clustering workflow.
     * 
     * @return a WorkflowResult containing the processed data and clustering results.
     */
    WorkflowResult execute();
}
