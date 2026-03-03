package org.example;

import ClusterBenchmark.ClusterBenchmarkFactory;
import ClusteringAlgorithms.ClusterAlgorithmFactory;
import Common.*;
import Enum.BenchmarkType;
import Enum.FileFormat;
import Enum.ReplicateCompressionType;
import Enum.ClusteringAlgorithmType;
import FileDataOperations.DataLoad;
import FileDataOperations.GeneClusterDataLoad;
import Filter.CompositeFilter;
import Filter.GeneFilterByTotalExpression;
import Filter.GeneFilterByVariance;
import Filter.SampleFilter;
import Filter.ZeroFilter;
import GeneDistance.CorrelationDistance;
import GeneDistance.JensenShannonDistance;
import Interfaces.*;
import LinkageCriteria.AverageLinkage;
import Normalizers.CompositeNormalizer;
import Normalizers.IRLS;
import Normalizers.ZScoreNormalizer;
import ReplicateCompression.ReplicateCompressionFactory;

import java.util.ArrayList;

public class ClusterTestService {
    static void main() {
        ///  TODO Set input files
        String outputFilePrefix = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\output";
        String geneExpressionFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\data.tsv";
        String metadataFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\metadata.tsv";

        FileFormat geneExpressionFileFormat = FileFormat.TSV;
        FileFormat metadataFileFormat = FileFormat.TSV;

        String replicateColumn = "Replicate";
        String timeSeriesColumn = "Time";
        String sampleColumn = "Sample";

        int numberOfReplicates = 3;
        int numberOfTimeSeries = 13;

        /// TODO: Set number of clusters and iterations
        int numberOfClusters = 4;
        int numberOfIterations = 1000;
        double fuzziness = 2.0;
        double epsilon = 0.001;

        ///  TODO: Set distance definition
        IGeneDistance geneDistance = new CorrelationDistance();

        ///  TODO: Set Cluster algorithm
        ClusteringAlgorithmType algorithmType = ClusteringAlgorithmType.KMeans;
        IClusteringAlgorithm algorithm = ClusterAlgorithmFactory.createKMeans(numberOfClusters, numberOfIterations, geneDistance);

        /// 1. Load Raw Data
        IDataLoad dataLoad = new DataLoad(
                geneExpressionFileName, 
                metadataFileName, 
                replicateColumn, 
                timeSeriesColumn, 
                sampleColumn, 
                numberOfReplicates, 
                numberOfTimeSeries, 
                geneExpressionFileFormat, 
                metadataFileFormat
        );
        GeneExpressionData rawData = dataLoad.getGeneExpressionFormattedData();

        /// 2. Prepare DataProcessor components
        
        // Set gene filters
        CompositeFilter geneFilter = new CompositeFilter();
        geneFilter.addfilter(new ZeroFilter());
        geneFilter.addfilter(new GeneFilterByTotalExpression(1));
        geneFilter.addfilter(new GeneFilterByVariance(1));

        // Set sample filters
        SampleFilter sampleFilter = new SampleFilter();
        // sampleFilter.addValidSampleTrait("TraitName", "TraitValue"); // Example usage

        // Set normalizers
        CompositeNormalizer normalizer = new CompositeNormalizer();
        normalizer.add(new IRLS(numberOfReplicates, numberOfTimeSeries));
        normalizer.add(new ZScoreNormalizer());

        // Set compression type
        ReplicateCompressionType replicateCompression = ReplicateCompressionType.Default;
        IReplicateCompression compression = ReplicateCompressionFactory.createReplicateCompression(replicateCompression);

        /// 3. Process Data
        DataProcessor dataProcessor = new DataProcessor(geneFilter, sampleFilter, compression, normalizer);
        GeneExpressionData processedData = dataProcessor.processData(rawData);

        /// TODO: Set benchmark
        BenchmarkType benchmarkType = BenchmarkType.Jaccard;
        String goldStandardFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\ground_truth.txt";
        GeneClusterData goldStandard = new GeneClusterDataLoad(goldStandardFileName).readClusterData();
        IClusterBenchmark benchmark = ClusterBenchmarkFactory.create(benchmarkType, geneDistance, goldStandard);

        RunSingleClusterAttempt(processedData, algorithm, outputFilePrefix, benchmark);
    }

    private static void RunSingleClusterAttempt(GeneExpressionData geneExpressionData, IClusteringAlgorithm algorithm, String basePrefix, IClusterBenchmark benchmark) {
        ClusterGenerationService.RunClustering(geneExpressionData, algorithm, basePrefix);
        String finalPrefix = basePrefix + ".txt";
        ClusterBenchmarkService.RunBenchmark(geneExpressionData, benchmark, finalPrefix);
    }
}
