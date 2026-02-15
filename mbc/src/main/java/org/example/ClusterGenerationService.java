package org.example;

import Common.FileFormat;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import Common.SampleTrait;
import FileDataOperations.GeneExpressionDataLoad;
import FileDataOperations.GeneClusterDataWrite;
import Interfaces.*;

import java.util.ArrayList;

public class ClusterGenerationService {
    static void RunClustering(String directoryPath, String geneExpressionFileName, String metadataFileName, int numberOfClusters, int numberOfIterations, ArrayList<IGeneFilter> geneFilters, ArrayList<SampleTrait> sampleFilters, ArrayList<IDataNormalizer> normalizers, IClusteringAlgorithm algorithm) {
        GeneExpressionData geneExpressionData = getGeneExpressionData(directoryPath, geneExpressionFileName, metadataFileName, geneFilters, sampleFilters, normalizers);

        GeneClusterData result = algorithm.clusterGenes(geneExpressionData);

        IGeneClusterDataWrite geneExpressionDataWrite = new GeneClusterDataWrite();
        geneExpressionDataWrite.writeClusteringDataToFile(result, directoryPath);
    }

    public static GeneExpressionData getGeneExpressionData(String directoryPath, String geneExpressionFileName, String metadataFileName,  ArrayList<IGeneFilter> geneFilters, ArrayList<SampleTrait> sampleFilters, ArrayList<IDataNormalizer> normalizers) {
        IGeneExpressionDataLoad geneExpressionDataSource = getGeneExpressionDataLoad(directoryPath, geneExpressionFileName, metadataFileName);

        for (IGeneFilter filter : geneFilters) {
            geneExpressionDataSource.addGeneFilter(filter);
        }

        for (SampleTrait sampleTrait : sampleFilters) {
            geneExpressionDataSource.addSampleFilter(sampleTrait.getTrait(), sampleTrait.getValue());
        }

        for (IDataNormalizer normalizer : normalizers) {
            geneExpressionDataSource.addNormalizer(normalizer);
        }

        return geneExpressionDataSource.getGeneExpressionFormattedData();
    }

    private static IGeneExpressionDataLoad getGeneExpressionDataLoad(String directoryPath, String geneExpressionFileName, String metadataFileName) {
        FileFormat geneExpressionFileFormat = FileFormat.TSV;
        FileFormat metadataFileFormat = FileFormat.CSV;
        String replicateColumn = "Replicate";
        String timeSeriesColumn = "Time";
        String sampleColumn = "Sample";

        int numberOfReplicates = 3;
        int numberOfTimeSeries = 13;

        return new GeneExpressionDataLoad(directoryPath, geneExpressionFileName, metadataFileName, replicateColumn, timeSeriesColumn, sampleColumn, numberOfReplicates, numberOfTimeSeries, geneExpressionFileFormat, metadataFileFormat);
    }
}
