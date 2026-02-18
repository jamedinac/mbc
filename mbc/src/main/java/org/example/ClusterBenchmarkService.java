package org.example;

import Common.*;
import FileDataOperations.GeneClusterDataLoad;
import Interfaces.IClusterBenchmark;
import Interfaces.IDataNormalizer;
import Interfaces.IGeneFilter;
import Interfaces.IReplicateCompression;
import Enum.FileFormat;

import java.util.ArrayList;

public class ClusterBenchmarkService {
    /**
     * Runs the cluster benchmark
     * @param directoryPath
     * @param geneExpressionFileName
     * @param metadataFileName
     * @param outputFileName
     * @param clusterBenchmark
     * @param geneFilter
     * @param sampleFilter
     * @param normalizers
     * @param replicateCompression
     * @param replicateColumn
     * @param timeSeriesColumn
     * @param sampleColumn
     * @param numberOfReplicates
     * @param numberOfTimeSeries
     * @param geneExpressionFileFormat
     * @param metadataFileFormat
     */
    public static void RunBenchmark(String directoryPath,
                                    String geneExpressionFileName,
                                    String metadataFileName,
                                    String outputFileName,
                                    IClusterBenchmark clusterBenchmark,
                                    ArrayList<IGeneFilter> geneFilter,
                                    ArrayList<SampleTrait> sampleFilter,
                                    ArrayList<IDataNormalizer> normalizers,
                                    IReplicateCompression replicateCompression,
                                    String replicateColumn,
                                    String timeSeriesColumn,
                                    String sampleColumn,
                                    int numberOfReplicates,
                                    int numberOfTimeSeries,
                                    FileFormat geneExpressionFileFormat,
                                    FileFormat metadataFileFormat
    ) {
        ClusterBenchmarkResult clusterBenchmarkResult = getClusterBenchmarkResult(directoryPath, geneExpressionFileName, metadataFileName, outputFileName, clusterBenchmark,  geneFilter, sampleFilter, normalizers, replicateCompression, replicateColumn, timeSeriesColumn, sampleColumn, numberOfReplicates, numberOfTimeSeries, geneExpressionFileFormat, metadataFileFormat);
        clusterBenchmarkResult.writeClusterBenchmarkToFile(directoryPath);
    }

    public static ClusterBenchmarkResult getClusterBenchmarkResult(String directoryPath,
                                                                   String geneExpressionFileName,
                                                                   String metadataFileName,
                                                                   String outputFileName,
                                                                   IClusterBenchmark clusterBenchmark,
                                                                   ArrayList<IGeneFilter> geneFilter,
                                                                   ArrayList<SampleTrait> sampleFilter,
                                                                   ArrayList<IDataNormalizer> normalizers,
                                                                   IReplicateCompression replicateCompression,
                                                                   String replicateColumn,
                                                                   String timeSeriesColumn,
                                                                   String sampleColumn,
                                                                   int numberOfReplicates,
                                                                   int numberOfTimeSeries,
                                                                   FileFormat geneExpressionFileFormat,
                                                                   FileFormat metadataFileFormat
    ) {
        GeneExpressionData geneExpressionData = ClusterGenerationService.getGeneExpressionData(directoryPath,  geneExpressionFileName, metadataFileName, geneFilter, sampleFilter, normalizers, replicateCompression, replicateColumn, timeSeriesColumn, sampleColumn, numberOfReplicates, numberOfTimeSeries, geneExpressionFileFormat, metadataFileFormat);

        GeneClusterDataLoad geneClusterDataLoad = new GeneClusterDataLoad(directoryPath, outputFileName);
        GeneClusterData clusterData = geneClusterDataLoad.readClusterData();

        return clusterBenchmark.evaluate(geneExpressionData, clusterData);
    }
}
