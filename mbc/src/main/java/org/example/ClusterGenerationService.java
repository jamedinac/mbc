package org.example;

import Common.*;
import FileDataOperations.GeneExpressionDataLoad;
import FileDataOperations.GeneClusterDataWrite;
import Interfaces.*;


public class ClusterGenerationService {

    static void RunClustering(ClusterGenerationInputData clusterGenerationInputData) {
        GeneExpressionData geneExpressionData = getGeneExpressionData(clusterGenerationInputData);

        GeneClusterData result = clusterGenerationInputData.getAlgorithm().clusterGenes(geneExpressionData);

        IGeneClusterDataWrite geneExpressionDataWrite = new GeneClusterDataWrite();
        geneExpressionDataWrite.writeClusteringDataToFile(result, clusterGenerationInputData.getDirectoryPath());
    }

    public static GeneExpressionData getGeneExpressionData(ClusterGenerationInputData clusterGenerationInputData) {
        IGeneExpressionDataLoad geneExpressionDataLoad = getGeneExpressionDataLoad(clusterGenerationInputData);

        for (IGeneFilter filter : clusterGenerationInputData.getGeneFilters()) {
            geneExpressionDataLoad.addGeneFilter(filter);
        }

        for (SampleTrait sampleTrait : clusterGenerationInputData.getSampleFilters()) {
            geneExpressionDataLoad.addSampleFilter(sampleTrait.getTrait(), sampleTrait.getValue());
        }

        for (IDataNormalizer normalizer : clusterGenerationInputData.getNormalizers()) {
            geneExpressionDataLoad.addNormalizer(normalizer);
        }

        geneExpressionDataLoad.setReplicatesCompressor(clusterGenerationInputData.getCompression());

        return geneExpressionDataLoad.getGeneExpressionFormattedData();
    }

    private static IGeneExpressionDataLoad getGeneExpressionDataLoad(ClusterGenerationInputData clusterGenerationInputData) {
        return new GeneExpressionDataLoad(
                clusterGenerationInputData.getDirectoryPath(),
                clusterGenerationInputData.getGeneExpressionFileName(),
                clusterGenerationInputData.getMetadataFileName(),
                clusterGenerationInputData.getReplicateColumn(),
                clusterGenerationInputData.getTimeSeriesColumn(),
                clusterGenerationInputData.getSampleColumn(),
                clusterGenerationInputData.getNumberOfReplicates(),
                clusterGenerationInputData.getNumberOfTimeSeries(),
                clusterGenerationInputData.getGeneExpressionFileFormat(),
                clusterGenerationInputData.getMetadataFileFormat()
        );
    }
}
