package org.example;

import ClusteringAlgorithms.ClusterAlgorithmFactory;
import Common.DataProcessor;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import Enum.ReplicateCompressionType;
import FileDataOperations.DataLoad;
import FileDataOperations.GeneClusterDataWrite;
import FileDataOperations.GeneExpressionDataWrite;
import Filter.CompositeFilter;
import Filter.GeneFilterByTotalExpression;
import Filter.GeneFilterByVariance;
import Filter.SampleFilter;
import Filter.ZeroFilter;
import GeneDistance.CorrelationDistance;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IDataLoad;
import Interfaces.IDataProcessor;
import Interfaces.IGeneClusterDataWrite;
import Interfaces.IGeneDistance;
import Interfaces.IReplicateCompression;
import Interfaces.ISampleFilter;
import Normalizers.CompositeNormalizer;
import Normalizers.IRLS;
import Normalizers.ZScoreNormalizer;
import ReplicateCompression.ReplicateCompressionFactory;

public class ClusterGenerationService {

    private static final String outputFilePrefix = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\output";
    private static final String geneExpressionFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\data.tsv";
    private static final String metadataFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\metadata.tsv";
    private static final String processedDataPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\processed_data.csv";

    private static final String replicateColumn = "Replicate";
    private static final String timeSeriesColumn = "Time";
    private static final String sampleColumn = "Sample";

    private static final int numberOfReplicates = 3;
    private static final int numberOfTimeSeries = 13;
    private static final int numberOfClusters = 4;
    private static final int numberOfIterations = 1000;

    public static void main(String[] args) {
        IGeneDistance geneDistance = new CorrelationDistance();

        // Data Access
        IDataLoad dataLoad = new DataLoad(
                geneExpressionFileName, 
                metadataFileName, 
                replicateColumn, 
                timeSeriesColumn, 
                sampleColumn, 
                numberOfReplicates, 
                numberOfTimeSeries
        );

        // Domain Rules
        CompositeFilter geneFilter = new CompositeFilter();
        geneFilter.addfilter(new ZeroFilter());
        geneFilter.addfilter(new GeneFilterByTotalExpression(1));
        geneFilter.addfilter(new GeneFilterByVariance(1));

        ISampleFilter sampleFilter = new SampleFilter();
        
        CompositeNormalizer normalizer = new CompositeNormalizer();
        normalizer.add(new IRLS(numberOfReplicates, numberOfTimeSeries));
        normalizer.add(new ZScoreNormalizer());

        IReplicateCompression compression = ReplicateCompressionFactory.createReplicateCompression(ReplicateCompressionType.Default);

        // Core Services
        IDataProcessor dataProcessor = new DataProcessor(geneFilter, sampleFilter, compression, normalizer);
        
        // Execute processing
        GeneExpressionData rawData = dataLoad.getGeneExpressionFormattedData();
        GeneExpressionData processedData = dataProcessor.processData(rawData);

        // Export processed data to CSV
        new GeneExpressionDataWrite().writeGeneExpressionDataToFile(processedData, processedDataPath);

        // Generate Clustering
        IClusteringAlgorithm algorithm = ClusterAlgorithmFactory.createKMeans(numberOfClusters, numberOfIterations, geneDistance);
        ClusterGenerationService generationService = new ClusterGenerationService();
        generationService.runClustering(processedData, algorithm, outputFilePrefix);
    }

    public void runClustering(GeneExpressionData geneExpressionData, IClusteringAlgorithm algorithm, String outputFilePrefix) {
        GeneClusterData result = algorithm.clusterGenes(geneExpressionData);

        IGeneClusterDataWrite geneExpressionDataWrite = new GeneClusterDataWrite();
        geneExpressionDataWrite.writeClusteringDataToFile(result, outputFilePrefix);
    }
}
