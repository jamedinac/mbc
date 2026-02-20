package Common;

import Enum.BenchmarkType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClusterBenchmarkResult {

    private final BenchmarkType benchmarkType;
    double[] benchmarkGeneValue;
    double benchmarkValue;
    GeneClusterData geneClusterData;

    public ClusterBenchmarkResult(BenchmarkType benchmarkType, double[] benchmarkGeneValue, double benchmarkValue, GeneClusterData geneClusterData) {
        this.benchmarkType = benchmarkType;
        this.benchmarkGeneValue = benchmarkGeneValue;
        this.benchmarkValue = benchmarkValue;
        this.geneClusterData = geneClusterData;
    }

    public void writeClusterBenchmarkToFile (String outputFilePrefix) {
        try {
            String clusterBenchmarkFile = this.getFileName(outputFilePrefix) +  "_" + this.benchmarkType + ".txt";
            StringBuilder fileContent = new StringBuilder();

            fileContent.append("Benchmark global result").append("\t").append(this.benchmarkValue).append("\n");
            fileContent.append("Gene ID").append("\t").append("Benchmark value").append("\n");

            for (int g = 0; g < geneClusterData.getNumberOfGenes(); g++) {
                fileContent.append(geneClusterData.getGeneId(g)).append("\t").append(this.benchmarkGeneValue[g]).append("\n");
            }

            Files.writeString(Paths.get(clusterBenchmarkFile), fileContent.toString());
        } catch (IOException e) {
            System.out.println("Error writing gene expression file: " + e.getMessage());
        }
    }

    public double getBenchmarkValue () {
        return benchmarkValue;
    }

    private String getFileName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return fileName.substring(0, dotIndex);
    }
}
