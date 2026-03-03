package FileDataOperations;

import BenchmarkResult.ClusterBenchmarkResult;
import BenchmarkResult.CompositeBenchmarkResult;
import Common.GeneClusterData;
import Enum.BenchmarkType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BenchmarkResultsWriter {

    public void write(CompositeBenchmarkResult compositeResult, String outputFilePrefix) {
        try {
            String fileName = getFileName(outputFilePrefix) + "_benchmarks.txt";
            StringBuilder fileContent = new StringBuilder();

            List<ClusterBenchmarkResult> results = compositeResult.getResults();

            // 1. Summary Section
            fileContent.append("=== BENCHMARK SUMMARY ===\n");
            for (ClusterBenchmarkResult result : results) {
                fileContent.append(result.getBenchmarkType()).append(":\t").append(result.getBenchmarkValue()).append("\n");
            }
            fileContent.append("\n");

            // 2. Specific Sections
            for (ClusterBenchmarkResult result : results) {
                fileContent.append("=== ").append(result.getBenchmarkType()).append(" ===\n");
                fileContent.append("Global Value:\t").append(result.getBenchmarkValue()).append("\n");

                if (result.getBenchmarkType() == BenchmarkType.Silhouette) {
                    fileContent.append("\nGene ID\tSilhouette Value\n");
                    GeneClusterData clusterData = result.getGeneClusterData();
                    double[] geneValues = result.getBenchmarkGeneValue();
                    
                    if (clusterData != null && geneValues != null) {
                        for (int g = 0; g < clusterData.getNumberOfGenes(); g++) {
                            fileContent.append(clusterData.getGeneId(g)).append("\t").append(geneValues[g]).append("\n");
                        }
                    }
                }
                
                fileContent.append("\n");
            }

            Files.writeString(Paths.get(fileName), fileContent.toString());
        } catch (IOException e) {
            System.out.println("Error writing benchmark file: " + e.getMessage());
        }
    }

    private String getFileName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(0, dotIndex);
        }
        return fileName;
    }
}
