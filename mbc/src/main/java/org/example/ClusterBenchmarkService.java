package org.example;

import Common.*;
import FileDataOperations.GeneClusterDataLoad;
import Interfaces.IClusterBenchmark;
import Interfaces.IDataNormalizer;
import Interfaces.IGeneFilter;

import java.util.ArrayList;

public class ClusterBenchmarkService {
    static void RunBenchmark(String directoryPath, String geneExpressionFileName, String metadataFileName, String outputFileName, IClusterBenchmark clusterBenchmark, ArrayList<IGeneFilter> geneFilter, ArrayList<SampleTrait> sampleFilter, ArrayList<IDataNormalizer> normalizers) {
        ClusterBenchmarkResult clusterBenchmarkResult = getClusterBenchmarkResult(directoryPath, geneExpressionFileName, metadataFileName, outputFileName, clusterBenchmark,  geneFilter, sampleFilter, normalizers);
        clusterBenchmarkResult.writeClusterBenchmarkToFile(directoryPath);
    }

    public static ClusterBenchmarkResult getClusterBenchmarkResult(String directoryPath, String geneExpressionFileName, String metadataFileName, String outputFileName, IClusterBenchmark clusterBenchmark, ArrayList<IGeneFilter> geneFilter, ArrayList<SampleTrait> sampleFilter, ArrayList<IDataNormalizer> normalizers) {
        GeneExpressionData geneExpressionData = ClusterGenerationService.getGeneExpressionData(directoryPath,  geneExpressionFileName, metadataFileName, geneFilter, sampleFilter, normalizers);

        GeneClusterDataLoad geneClusterDataLoad = new GeneClusterDataLoad(directoryPath, outputFileName);
        GeneClusterData clusterData = geneClusterDataLoad.readClusterData();

        return clusterBenchmark.evaluate(geneExpressionData, clusterData);
    }
}
