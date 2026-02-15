package org.example;

import Common.*;
import FileDataOperations.GeneClusterDataLoad;
import Interfaces.IClusterBenchmark;
import Interfaces.IDataNormalizer;
import Interfaces.IGeneFilter;
import Interfaces.IReplicateCompression;

import java.util.ArrayList;

public class ClusterBenchmarkService {
    static void RunBenchmark(String directoryPath, String geneExpressionFileName, String metadataFileName, String outputFileName, IClusterBenchmark clusterBenchmark, ArrayList<IGeneFilter> geneFilter, ArrayList<SampleTrait> sampleFilter, ArrayList<IDataNormalizer> normalizers, IReplicateCompression replicateCompression) {
        ClusterBenchmarkResult clusterBenchmarkResult = getClusterBenchmarkResult(directoryPath, geneExpressionFileName, metadataFileName, outputFileName, clusterBenchmark,  geneFilter, sampleFilter, normalizers, replicateCompression);
        clusterBenchmarkResult.writeClusterBenchmarkToFile(directoryPath);
    }

    public static ClusterBenchmarkResult getClusterBenchmarkResult(String directoryPath, String geneExpressionFileName, String metadataFileName, String outputFileName, IClusterBenchmark clusterBenchmark, ArrayList<IGeneFilter> geneFilter, ArrayList<SampleTrait> sampleFilter, ArrayList<IDataNormalizer> normalizers, IReplicateCompression replicateCompression) {
        GeneExpressionData geneExpressionData = ClusterGenerationService.getGeneExpressionData(directoryPath,  geneExpressionFileName, metadataFileName, geneFilter, sampleFilter, normalizers, replicateCompression);

        GeneClusterDataLoad geneClusterDataLoad = new GeneClusterDataLoad(directoryPath, outputFileName);
        GeneClusterData clusterData = geneClusterDataLoad.readClusterData();

        return clusterBenchmark.evaluate(geneExpressionData, clusterData);
    }
}
