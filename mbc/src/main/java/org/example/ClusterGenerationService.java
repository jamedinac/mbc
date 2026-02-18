package org.example;

import Enum.FileFormat;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import Common.SampleTrait;
import FileDataOperations.GeneExpressionDataLoad;
import FileDataOperations.GeneClusterDataWrite;
import Interfaces.*;

import java.util.ArrayList;

public class ClusterGenerationService {
    /**
     * Runs the clustering algorithm
     * @param directoryPath
     * @param geneExpressionFileName
     * @param metadataFileName
     * @param geneFilters
     * @param sampleFilters
     * @param normalizers
     * @param algorithm
     * @param replicateCompression
     * @param replicateColumn
     * @param timeSeriesColumn
     * @param sampleColumn
     * @param numberOfReplicates
     * @param numberOfTimeSeries
     * @param geneExpressionFileFormat
     * @param metadataFileFormat
     */
    static void RunClustering(String directoryPath,
                              String geneExpressionFileName,
                              String metadataFileName,
                              ArrayList<IGeneFilter> geneFilters,
                              ArrayList<SampleTrait> sampleFilters,
                              ArrayList<IDataNormalizer> normalizers,
                              IClusteringAlgorithm algorithm,
                              IReplicateCompression replicateCompression,
                              String replicateColumn,
                              String timeSeriesColumn,
                              String sampleColumn,
                              int numberOfReplicates,
                              int numberOfTimeSeries,
                              FileFormat geneExpressionFileFormat,
                              FileFormat metadataFileFormat
    ) {
        GeneExpressionData geneExpressionData = getGeneExpressionData(directoryPath, geneExpressionFileName, metadataFileName, geneFilters, sampleFilters, normalizers, replicateCompression, replicateColumn, timeSeriesColumn, sampleColumn, numberOfReplicates, numberOfTimeSeries,  geneExpressionFileFormat, metadataFileFormat);

        GeneClusterData result = algorithm.clusterGenes(geneExpressionData);

        IGeneClusterDataWrite geneExpressionDataWrite = new GeneClusterDataWrite();
        geneExpressionDataWrite.writeClusteringDataToFile(result, directoryPath);
    }

    /**
     * Builds the gene expresion data model
     * @param directoryPath
     * @param geneExpressionFileName
     * @param metadataFileName
     * @param geneFilters
     * @param sampleFilters
     * @param normalizers
     * @param replicateCompression
     * @param replicateColumn
     * @param timeSeriesColumn
     * @param sampleColumn
     * @param numberOfReplicates
     * @param numberOfTimeSeries
     * @param geneExpressionFileFormat
     * @param metadataFileFormat
     * @return
     */
    public static GeneExpressionData getGeneExpressionData(String directoryPath,
                                                           String geneExpressionFileName,
                                                           String metadataFileName,
                                                           ArrayList<IGeneFilter> geneFilters,
                                                           ArrayList<SampleTrait> sampleFilters,
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
        IGeneExpressionDataLoad geneExpressionDataLoad = getGeneExpressionDataLoad(directoryPath, geneExpressionFileName, metadataFileName, replicateColumn, timeSeriesColumn, sampleColumn, numberOfReplicates, numberOfTimeSeries, geneExpressionFileFormat, metadataFileFormat);

        for (IGeneFilter filter : geneFilters) {
            geneExpressionDataLoad.addGeneFilter(filter);
        }

        for (SampleTrait sampleTrait : sampleFilters) {
            geneExpressionDataLoad.addSampleFilter(sampleTrait.getTrait(), sampleTrait.getValue());
        }

        for (IDataNormalizer normalizer : normalizers) {
            geneExpressionDataLoad.addNormalizer(normalizer);
        }

        geneExpressionDataLoad.setReplicatesCompressor(replicateCompression);

        return geneExpressionDataLoad.getGeneExpressionFormattedData();
    }

    private static IGeneExpressionDataLoad getGeneExpressionDataLoad(String directoryPath,
                                                                     String geneExpressionFileName,
                                                                     String metadataFileName,
                                                                     String replicateColumn,
                                                                     String timeSeriesColumn,
                                                                     String sampleColumn,
                                                                     int numberOfReplicates,
                                                                     int numberOfTimeSeries,
                                                                     FileFormat geneExpressionFileFormat,
                                                                     FileFormat metadataFileFormat
    ) {
        return new GeneExpressionDataLoad(directoryPath, geneExpressionFileName, metadataFileName, replicateColumn, timeSeriesColumn, sampleColumn, numberOfReplicates, numberOfTimeSeries, geneExpressionFileFormat, metadataFileFormat);
    }
}
