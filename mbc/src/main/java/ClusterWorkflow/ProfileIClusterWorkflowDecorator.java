package ClusterWorkflow;

import Common.GeneExpressionData;
import Common.InputSummary;
import Common.WorkflowResult;
import Enum.FilterStatus;
import Interfaces.IClusterWorkflow;
import Interfaces.IDataWriter;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Decorator class for ClusterWorkflow that records the total computational
 * time, peak heap memory usage, and the number of genes processed.
 */
public class ProfileIClusterWorkflowDecorator implements IClusterWorkflow {
    private final IClusterWorkflow wrappedWorkflow;
    private final IDataWriter dataWriter;
    private static final String METRICS_FILE_NAME = "profile_metrics.json";

    public ProfileIClusterWorkflowDecorator(IClusterWorkflow wrappedWorkflow, IDataWriter dataWriter) {
        this.wrappedWorkflow = wrappedWorkflow;
        this.dataWriter = dataWriter;
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
        this.writeMetrics(durationNanos, peakMemoryMB, inputSummary, processedData, result.getFilteredOutGenes());

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

    private void writeMetrics(long durationNanos, double peakMemoryMB, InputSummary inputSummary, 
                              GeneExpressionData processedData, Map<String, FilterStatus> filteredOutGenes) {
        double durationSeconds = durationNanos / 1_000_000_000.0;

        Map<String, Object> rootMap = new LinkedHashMap<>();
        
        Map<String, Object> profilingMetrics = new LinkedHashMap<>();
        profilingMetrics.put("input_genes", inputSummary.getGeneCount());
        profilingMetrics.put("input_samples", inputSummary.getSampleCount());
        profilingMetrics.put("clustered_genes", processedData.getNumberOfGenes());
        profilingMetrics.put("time_series", processedData.getSampleIds().length);
        profilingMetrics.put("execution_time_seconds", durationSeconds);
        profilingMetrics.put("peak_heap_memory_mb", peakMemoryMB);
        
        rootMap.put("profiling_metrics", profilingMetrics);
        
        Map<String, String> stringFilteredOutGenes = new LinkedHashMap<>();
        for (Map.Entry<String, FilterStatus> entry : filteredOutGenes.entrySet()) {
            stringFilteredOutGenes.put(entry.getKey(), entry.getValue().name());
        }
        rootMap.put("filtered_out_genes", stringFilteredOutGenes);

        this.dataWriter.writeData(rootMap, METRICS_FILE_NAME);
    }
}
