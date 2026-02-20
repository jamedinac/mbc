package org.example;

import Common.*;
import FileDataOperations.GeneExpressionDataLoad;
import FileDataOperations.GeneClusterDataWrite;
import Interfaces.*;


public class ClusterGenerationService {

    static void RunClustering(ClusterParameters clusterParameters) {
        GeneExpressionData geneExpressionData = getGeneExpressionData(clusterParameters);

        GeneClusterData result = clusterParameters.getAlgorithm().clusterGenes(geneExpressionData);

        IGeneClusterDataWrite geneExpressionDataWrite = new GeneClusterDataWrite();
        geneExpressionDataWrite.writeClusteringDataToFile(result, clusterParameters.getOutputFilePrefix());
    }

    public static GeneExpressionData getGeneExpressionData(ClusterParameters clusterParameters) {
        IGeneExpressionDataLoad geneExpressionDataLoad = getGeneExpressionDataLoad(clusterParameters);

        for (IGeneFilter filter : clusterParameters.getGeneFilters()) {
            geneExpressionDataLoad.addGeneFilter(filter);
        }

        for (SampleTrait sampleTrait : clusterParameters.getSampleFilters()) {
            geneExpressionDataLoad.addSampleFilter(sampleTrait.getTrait(), sampleTrait.getValue());
        }

        for (IDataNormalizer normalizer : clusterParameters.getNormalizers()) {
            geneExpressionDataLoad.addNormalizer(normalizer);
        }

        geneExpressionDataLoad.setReplicatesCompressor(clusterParameters.getCompression());

        return geneExpressionDataLoad.getGeneExpressionFormattedData();
    }

    private static IGeneExpressionDataLoad getGeneExpressionDataLoad(ClusterParameters clusterParameters) {
        return new GeneExpressionDataLoad(
                clusterParameters.getGeneExpressionFileName(),
                clusterParameters.getMetadataFileName(),
                clusterParameters.getReplicateColumn(),
                clusterParameters.getTimeSeriesColumn(),
                clusterParameters.getSampleColumn(),
                clusterParameters.getNumberOfReplicates(),
                clusterParameters.getNumberOfTimeSeries(),
                clusterParameters.getGeneExpressionFileFormat(),
                clusterParameters.getMetadataFileFormat()
        );
    }
}
