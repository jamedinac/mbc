package ClusterWorkflow;

import Common.GeneExpressionData;
import Common.InputSummary;
import Common.WorkflowResult;
import Interfaces.IClusterWorkflow;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Decorator class for ClusterWorkflow that records the total computational
 * time, peak heap memory usage, and the number of genes processed.
 */
public class ProfileIClusterWorkflowDecorator implements IClusterWorkflow {
    private final IClusterWorkflow wrappedWorkflow;
    private static final String METRICS_FILE_NAME = "profile_metrics.txt";

    public ProfileIClusterWorkflowDecorator(IClusterWorkflow wrappedWorkflow) {
        this.wrappedWorkflow = wrappedWorkflow;
    }

    @Override
    public WorkflowResult execute() {
        // Prepare for profiling
        this.resetPeakMemoryUsage();
        long startTime = System.nanoTime();

        // Delegate execution
        WorkflowResult result = wrappedWorkflow.execute();

        // Capture results
        long endTime = System.nanoTime();
        long durationNanos = endTime - startTime;
        double peakMemoryMB = this.getPeakHeapMemoryMB();

        InputSummary inputSummary = result.getInputSummary();
        GeneExpressionData processedData = result.getProcessedData();

        // Persist metrics (Overwrites the file)
        this.writeMetrics(durationNanos, peakMemoryMB, inputSummary, processedData);

        return result;
    }

    private void resetPeakMemoryUsage() {
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            pool.resetPeakUsage();
        }
    }

    private double getPeakHeapMemoryMB() {
        long peakMemoryBytes = 0;
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getType() == MemoryType.HEAP) {
                peakMemoryBytes += pool.getPeakUsage().getUsed();
            }
        }
        return peakMemoryBytes / (1024.0 * 1024.0);
    }

    private void writeMetrics(long durationNanos, double peakMemoryMB, InputSummary inputSummary, GeneExpressionData processedData) {
        double durationSeconds = durationNanos / 1_000_000_000.0;

        StringBuilder sb = new StringBuilder();
        sb.append("--- Profiling Results ---\n");
        sb.append(String.format("Input Genes: %d\n", inputSummary.getGeneCount()));
        sb.append(String.format("Input Samples: %d\n", inputSummary.getSampleCount()));
        sb.append(String.format("Clustered Genes: %d\n", processedData.getNumberOfGenes()));
        sb.append(String.format("Time Series: %d\n", processedData.getSampleIds().length));
        sb.append(String.format("Execution Time: %.4f seconds\n", durationSeconds));
        sb.append(String.format("Peak Heap Memory Usage: %.2f MB\n", peakMemoryMB));
        sb.append("-------------------------\n");

        sb.append("\n--- Retained Genes ---\n");
        for (String geneId : processedData.getGeneIds()) {
            sb.append(geneId).append("\n");
        }


        try {
            Files.writeString(Paths.get(METRICS_FILE_NAME), sb.toString(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            System.err.println("Failed to write profiling metrics to file: " + e.getMessage());
        }
    }
}
