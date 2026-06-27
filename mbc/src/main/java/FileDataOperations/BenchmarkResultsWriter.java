package FileDataOperations;

import BenchmarkResult.ClusterBenchmarkResult;
import BenchmarkResult.CompositeBenchmarkResult;
import Common.GeneClusterData;
import Enum.BenchmarkType;

import Interfaces.IDataWriter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BenchmarkResultsWriter {

    private final IDataWriter dataWriter;

    public BenchmarkResultsWriter(IDataWriter dataWriter) {
        this.dataWriter = dataWriter;
    }

    public void write(CompositeBenchmarkResult compositeResult, String fileName) {
        List<ClusterBenchmarkResult> results = compositeResult.getResults();
        Map<String, Object> rootMap = new LinkedHashMap<>();

        for (ClusterBenchmarkResult result : results) {
            String benchmarkName = result.getBenchmarkType().name().toLowerCase();
            
            if (result.getBenchmarkType() == BenchmarkType.Silhouette) {
                Map<String, Object> silhouetteMap = new LinkedHashMap<>();
                silhouetteMap.put("global_value", result.getBenchmarkValue());
                
                GeneClusterData clusterData = result.getGeneClusterData();
                double[] geneValues = result.getBenchmarkGeneValue();
                
                if (clusterData != null && geneValues != null) {
                    Map<String, Double> geneScores = new LinkedHashMap<>();
                    for (int g = 0; g < clusterData.getNumberOfGenes(); g++) {
                        geneScores.put(clusterData.getGeneId(g), geneValues[g]);
                    }
                    silhouetteMap.put("gene_scores", geneScores);
                }
                
                rootMap.put(benchmarkName, silhouetteMap);
            } else {
                rootMap.put(benchmarkName, result.getBenchmarkValue());
            }
        }

        this.dataWriter.writeData(rootMap, fileName);
    }
}
