package ClusterWorkflow;

import Common.WorkflowResult;
import Interfaces.ClusterWorkflow;
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
public class ProfileClusterWorkflowDecorator implements ClusterWorkflow {
    private final ClusterWorkflow wrappedWorkflow;
    private static final String METRICS_FILE_NAME = "profile_metrics.txt";

    public ProfileClusterWorkflowDecorator(ClusterWorkflow wrappedWorkflow) {
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
        int geneCount = result.getProcessedData().getNumberOfGenes();

        // Persist metrics (Overwrites the file)
        this.writeMetrics(durationNanos, peakMemoryMB, geneCount);

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

    private void writeMetrics(long durationNanos, double peakMemoryMB, int geneCount) {
        double durationSeconds = durationNanos / 1_000_000_000.0;
        String metricsReport = String.format(
                "--- Profiling Results ---\nGenes Processed: %d\nExecution Time: %.4f seconds\nPeak Heap Memory Usage: %.2f MB\n-------------------------\n",
                geneCount, durationSeconds, peakMemoryMB
        );

        try {
            // StandardOpenOption.TRUNCATE_EXISTING ensures the file is rewritten
            Files.writeString(Paths.get(METRICS_FILE_NAME), metricsReport, 
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            System.err.println("Failed to write profiling metrics to file: " + e.getMessage());
        }
    }
}
