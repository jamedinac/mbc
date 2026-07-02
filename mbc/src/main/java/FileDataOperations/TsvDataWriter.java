package FileDataOperations;

import Interfaces.IDataWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Implementation of IDataWriter that serializes maps to TSV (Tab-Separated Values) format.
 */
public class TsvDataWriter implements IDataWriter {

    @Override
    @SuppressWarnings("unchecked")
    public void writeData(Object data, String filePath) {
        if (!(data instanceof Map)) {
            System.err.println("TsvDataWriter requires a Map data structure.");
            return;
        }

        Map<String, Object> map = (Map<String, Object>) data;
        StringBuilder sb = new StringBuilder();

        try {
            if (map.containsKey("profiling_metrics")) {
                // Formatting profiling metrics and filtered out genes
                sb.append("Category\tMetric\tValue\n");
                
                Object profilingMetricsObj = map.get("profiling_metrics");
                if (profilingMetricsObj instanceof Map) {
                    Map<String, Object> profilingMetrics = (Map<String, Object>) profilingMetricsObj;
                    for (Map.Entry<String, Object> entry : profilingMetrics.entrySet()) {
                        sb.append("profiling_metrics\t").append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
                    }
                }

                Object filteredOutGenesObj = map.get("filtered_out_genes");
                if (filteredOutGenesObj instanceof Map) {
                    Map<String, Object> filteredOutGenes = (Map<String, Object>) filteredOutGenesObj;
                    for (Map.Entry<String, Object> entry : filteredOutGenes.entrySet()) {
                        sb.append("filtered_out_genes\t").append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
                    }
                }
            } else {
                // Formatting benchmark results
                sb.append("benchmark\tgeneid\tvalue\n");
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String benchmarkName = entry.getKey();
                    Object value = entry.getValue();

                    if (value instanceof Map) {
                        Map<String, Object> innerMap = (Map<String, Object>) value;
                        
                        // Print global value if exists
                        if (innerMap.containsKey("global_value")) {
                            sb.append(benchmarkName).append("\tglobal\t").append(innerMap.get("global_value")).append("\n");
                        }
                        
                        // Print gene scores if exists
                        if (innerMap.containsKey("gene_scores")) {
                            Object geneScoresObj = innerMap.get("gene_scores");
                            if (geneScoresObj instanceof Map) {
                                Map<String, Object> geneScores = (Map<String, Object>) geneScoresObj;
                                for (Map.Entry<String, Object> geneEntry : geneScores.entrySet()) {
                                    sb.append(benchmarkName).append("\t").append(geneEntry.getKey()).append("\t").append(geneEntry.getValue()).append("\n");
                                }
                            }
                        }
                    } else {
                        // Regular metric value
                        sb.append(benchmarkName).append("\tglobal\t").append(value).append("\n");
                    }
                }
            }

            Files.writeString(Paths.get(filePath), sb.toString());
        } catch (IOException e) {
            System.err.println("Error writing TSV file to " + filePath + ": " + e.getMessage());
        }
    }
}
