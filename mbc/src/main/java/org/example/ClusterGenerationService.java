package org.example;

import ClusteringAlgorithms.KMeansAlgorithm;
import Common.FileFormat;
import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import GeneExpressionDataOperation.GeneExpressionDataLoad;
import GeneExpressionDataOperation.GeneExpressionDataWrite;
import Filter.GeneFilterByTotalExpression;
import Filter.GeneFilterByVariance;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IGeneExpressionDataLoad;
import Interfaces.IGeneExpressionDataWrite;
import Normalizers.PseudologarithmNormalizer;
import Normalizers.ZScoreNormalizer;

public class ClusterGenerationService {
    static void main() {
        String directoryPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\IR64";

        IGeneExpressionDataLoad geneExpressionDataSource = getIGeneExpressionDataLoad(directoryPath);

        geneExpressionDataSource.addGeneFilter(new GeneFilterByTotalExpression(1));
        geneExpressionDataSource.addGeneFilter(new GeneFilterByVariance(1));

        geneExpressionDataSource.addSampleFilter("Drought_Group", "Severe");
        geneExpressionDataSource.addSampleFilter("Condition", "Drought");

        geneExpressionDataSource.addNormalizer(new PseudologarithmNormalizer());
        geneExpressionDataSource.addNormalizer(new ZScoreNormalizer());

        GeneExpressionData geneExpressionData = geneExpressionDataSource.getGeneExpressionFormattedData();

        IClusteringAlgorithm kMeans = new KMeansAlgorithm(20, 100);
        GeneClusteringResult kMeansResult = kMeans.clusterGenes(geneExpressionData);

        IGeneExpressionDataWrite geneExpressionDataWrite = new GeneExpressionDataWrite();
        geneExpressionDataWrite.writeClusteringDataToFile(kMeansResult, directoryPath);
    }

    private static IGeneExpressionDataLoad getIGeneExpressionDataLoad(String directoryPath) {
        String geneExpressionFileName = "data.txt";
        String metadataFileName = "metadata.txt";
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
