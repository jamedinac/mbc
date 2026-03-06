package org.example;

import ClusteringAlgorithms.ClusterAlgorithmFactory;
import Common.*;
import Enum.ReplicateCompressionType;
import FileDataOperations.DataLoad;
import FileDataOperations.GeneClusterDataLoad;
import Filter.CompositeFilter;
import Filter.GeneFilterByTotalExpression;
import Filter.GeneFilterByVariance;
import Filter.SampleFilter;
import Filter.ZeroFilter;
import GeneDistance.CorrelationDistance;
import Interfaces.*;
import Normalizers.CompositeNormalizer;
import Normalizers.IRLS;
import Normalizers.ZScoreNormalizer;
import ReplicateCompression.ReplicateCompressionFactory;

public class App {

    // 1. Hardcoded Configuration (To be moved to args/properties later)
    private static final String outputFilePrefix = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\output";
    private static final String geneExpressionFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\data.tsv";
    private static final String metadataFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\metadata.tsv";
    private static final String goldStandardFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\ground_truth.txt";

    private static final String replicateColumn = "Replicate";
    private static final String timeSeriesColumn = "Time";
    private static final String sampleColumn = "Sample";

    private static final int numberOfReplicates = 3;
    private static final int numberOfTimeSeries = 13;
    private static final int numberOfClusters = 4;
    private static final int numberOfIterations = 1000;

    static void main(String[] args) {
        IGeneDistance geneDistance = new CorrelationDistance();

        // 2. Build the Object Graph (Composition Root)
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
        //sampleFilter.addValidSampleTrait("Drought_Group", "Severe");
        //sampleFilter.addValidSampleTrait("Condition", "Drought");

        
        CompositeNormalizer normalizer = new CompositeNormalizer();
        normalizer.add(new IRLS(numberOfReplicates, numberOfTimeSeries));
        normalizer.add(new ZScoreNormalizer());

        IReplicateCompression compression = ReplicateCompressionFactory.createReplicateCompression(ReplicateCompressionType.Default);

        // Core Services
        IDataProcessor dataProcessor = new DataProcessor(geneFilter, sampleFilter, compression, normalizer);
        IClusterGenerationService generationService = new ClusterGenerationService();
        IClusterBenchmarkService benchmarkService = new ClusterBenchmarkService();
        
        // Use Case / Orchestrator
        IClusterOrchestrator orchestrator = new ClusterOrchestrator(dataProcessor, generationService, benchmarkService);

        // 3. Execute
        GeneExpressionData rawData = dataLoad.getGeneExpressionFormattedData();
        IClusteringAlgorithm algorithm = ClusterAlgorithmFactory.createKMeans(numberOfClusters, numberOfIterations, geneDistance);
        
        GeneClusterDataLoad goldStandardLoader = new GeneClusterDataLoad(goldStandardFileName);
        GeneClusterData goldStandard = goldStandardLoader.readClusterData();
        
        ClusterBenchmark.CompositeBenchmark compositeBenchmark = new ClusterBenchmark.CompositeBenchmark();
        compositeBenchmark.addBenchmark(new ClusterBenchmark.Jaccard(goldStandard));
        compositeBenchmark.addBenchmark(new ClusterBenchmark.Silhouette(geneDistance));
        compositeBenchmark.addBenchmark(new ClusterBenchmark.Accuracy(goldStandard));
        compositeBenchmark.addBenchmark(new ClusterBenchmark.AdjustedRandIndex(goldStandard));
        compositeBenchmark.addBenchmark(new ClusterBenchmark.NMI(goldStandard));


        orchestrator.executePipeline(rawData, algorithm, compositeBenchmark, outputFilePrefix);
    }
}
