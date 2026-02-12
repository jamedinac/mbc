package org.example;

import ClusterBenchmark.Silhouette;
import ClusteringAlgorithms.KMeansAlgorithm;
import Common.ClusterBenchmarkResult;
import Common.FileFormat;
import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import FileDataOperations.GeneExpressionDataLoad;
import FileDataOperations.GeneExpressionDataWrite;
import Filter.GeneFilterByTotalExpression;
import Filter.GeneFilterByVariance;
import Interfaces.IClusterBenchmark;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IGeneExpressionDataLoad;
import Interfaces.IGeneExpressionDataWrite;
import Normalizers.MedianRatiosNormalization;
import Normalizers.PseudologarithmNormalizer;
import Normalizers.ZScoreNormalizer;

public class ClusterGenerationService {
    static void main() {
        String directoryPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\IR64";

        GeneExpressionData geneExpressionData = getGeneExpressionData(directoryPath);

        IClusteringAlgorithm kMeans = new KMeansAlgorithm(10, 20);
        GeneClusteringResult kMeansResult = kMeans.clusterGenes(geneExpressionData);

        IGeneExpressionDataWrite geneExpressionDataWrite = new GeneExpressionDataWrite();
        geneExpressionDataWrite.writeClusteringDataToFile(kMeansResult, directoryPath);

        IClusterBenchmark silhouetteClustering = new Silhouette(kMeansResult);
        ClusterBenchmarkResult clusterBenchmarkResult = silhouetteClustering.evaluate();

        clusterBenchmarkResult.writeClusterBenchmarkToFile(directoryPath);
    }

    public static GeneExpressionData getGeneExpressionData(String directoryPath) {
        IGeneExpressionDataLoad geneExpressionDataSource = getGeneExpressionDataLoad(directoryPath);

        geneExpressionDataSource.addGeneFilter(new GeneFilterByTotalExpression(1));
        geneExpressionDataSource.addGeneFilter(new GeneFilterByVariance(1));

        geneExpressionDataSource.addSampleFilter("Drought_Group", "Severe");
        geneExpressionDataSource.addSampleFilter("Condition", "Drought");

        // geneExpressionDataSource.addNormalizer(new PseudologarithmNormalizer());
        // geneExpressionDataSource.addNormalizer(new ZScoreNormalizer());
        geneExpressionDataSource.addNormalizer(new MedianRatiosNormalization());

        return geneExpressionDataSource.getGeneExpressionFormattedData();
    }

    private static IGeneExpressionDataLoad getGeneExpressionDataLoad(String directoryPath) {
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
