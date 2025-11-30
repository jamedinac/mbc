package org.example;

import ClusterBenchmark.ClusterBenchmarkFactory;
import Common.BenchmarkType;
import Common.GeneClusteringResult;
import Interfaces.IClusterBenchmark;

import java.util.ArrayList;

public class ClusterBenchmarkService {
    static void main() {
        GeneClusteringResult geneClusteringResult = null;
        GeneClusteringResult goldStandard = null;

        IClusterBenchmark clusterBenchmark = null;
        ArrayList<ClusterBenchmarkService> clusterBenchmarkResults = new ArrayList<>();

        for (BenchmarkType benchmarkType : BenchmarkType.values()) {
            clusterBenchmark = ClusterBenchmarkFactory.create(benchmarkType, geneClusteringResult, goldStandard);
            clusterBenchmarkResults.add(clusterBenchmark.evaluate());
        }
    }
}
