package org.example;

import Common.BenchmarkType;
import Common.GeneClusteringResult;
import Interfaces.IClusterBenchmark;

import java.util.ArrayList;

public class ClusterBenchmark {
    static void main() {
        GeneClusteringResult geneClusteringResult = null;
        GeneClusteringResult goldStandard = null;

        IClusterBenchmark clusterBenchmark = null;
        ArrayList<ClusterBenchmark> clusterBenchmarkResults = new ArrayList<>();

        for (BenchmarkType benchmarkType : BenchmarkType.values()) {
            clusterBenchmark =
            clusterBenchmarkResults.add(clusterBenchmark.evaluate());
        }
    }
}
