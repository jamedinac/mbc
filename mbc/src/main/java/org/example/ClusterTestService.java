package org.example;

import ClusterBenchmark.ClusterBenchmarkFactory;
import ClusteringAlgorithms.ClusterAlgorithmFactory;
import Common.*;
import Enum.BenchmarkType;
import Enum.FileFormat;
import Enum.ReplicateCompressionType;
import Enum.ClusteringAlgorithmType;
import Filter.GeneFilterByTotalExpression;
import Filter.GeneFilterByVariance;
import Filter.ZeroFilter;
import GeneDistance.CorrelationDistance;
import GeneDistance.EuclideanDistance;
import GeneDistance.JensenShannonDistance;
import Interfaces.*;
import Normalizers.EnthropyNormalizer;
import Normalizers.MedianRatiosNormalization;
import ReplicateCompression.ReplicateCompressionFactory;

import java.util.ArrayList;

public class ClusterTestService {
    static void main() {
        String directoryPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated";
        String outputFileName = "output.txt";
        String geneExpressionFileName = "data.csv";
        String metadataFileName = "metadata.csv";

        FileFormat geneExpressionFileFormat = FileFormat.CSV;
        FileFormat metadataFileFormat = FileFormat.CSV;
        String replicateColumn = "Replicate";
        String timeSeriesColumn = "Time";
        String sampleColumn = "Sample";

        int numberOfReplicates = 3;
        int numberOfTimeSeries = 13;

        int numberOfClusters = 4;
        int numberOfIterations = 50;

        ArrayList<IGeneFilter> geneFilters = new ArrayList<>();
        geneFilters.add(new ZeroFilter());
        geneFilters.add(new GeneFilterByTotalExpression(1));
        geneFilters.add(new GeneFilterByVariance(1));

        ArrayList<SampleTrait>  sampleFilters = new ArrayList<>();

        ArrayList<IDataNormalizer> normalizers = new ArrayList<>();
        normalizers.add(new MedianRatiosNormalization());

        IGeneDistance geneDistance = new EuclideanDistance();

        ClusteringAlgorithmType algorithmType = ClusteringAlgorithmType.KMeans;
        IClusteringAlgorithm algorithm = ClusterAlgorithmFactory.getClusteringAlgorithm(algorithmType, numberOfClusters, numberOfIterations, geneDistance);

        BenchmarkType benchmarkType = BenchmarkType.Silhouette;
        IClusterBenchmark benchmark = ClusterBenchmarkFactory.create(benchmarkType, geneDistance, null);

        ReplicateCompressionType replicateCompression = ReplicateCompressionType.Default;
        IReplicateCompression compression = ReplicateCompressionFactory.createReplicateCompression(replicateCompression);

        ClusterGenerationInputData clusterGenerationInputData = new ClusterGenerationInputData(directoryPath, geneExpressionFileName, metadataFileName, geneExpressionFileFormat, metadataFileFormat, replicateColumn, timeSeriesColumn, sampleColumn, numberOfReplicates, numberOfTimeSeries, compression, geneFilters, sampleFilters, normalizers, algorithmType, algorithm);
        ClusterBenchmarkInputData clusterBenchmarkInputData = new ClusterBenchmarkInputData(directoryPath, geneExpressionFileName, metadataFileName, geneExpressionFileFormat, metadataFileFormat, replicateColumn, timeSeriesColumn, sampleColumn, numberOfReplicates, numberOfTimeSeries, compression, geneFilters, sampleFilters, normalizers, outputFileName, benchmark);

        //RunSeveralClustersAttempt(clusterGenerationInputData, clusterBenchmarkInputData, 1, 20, numberOfIterations, algorithmType, geneDistance);
        RunSingleClusterAttempt(clusterGenerationInputData, clusterBenchmarkInputData);
    }

    private static void RunSingleClusterAttempt(ClusterGenerationInputData clusterGenerationInputData, ClusterBenchmarkInputData clusterBenchmarkInputData) {
        ClusterGenerationService.RunClustering(clusterGenerationInputData);
        ClusterBenchmarkService.RunBenchmark(clusterBenchmarkInputData);
    }

    private static void RunSeveralClustersAttempt(ClusterGenerationInputData clusterGenerationInputData, ClusterBenchmarkInputData clusterBenchmarkInputData, int startCluster, int endCluster, int numberOfIterations, ClusteringAlgorithmType algorithmType, IGeneDistance geneDistance) {
        for (int c = startCluster; c <= endCluster; c++) {
            clusterGenerationInputData.setAlgorithm(ClusterAlgorithmFactory.getClusteringAlgorithm(algorithmType, c, numberOfIterations, geneDistance));

            ClusterGenerationService.RunClustering(clusterGenerationInputData);
            System.out.println(c + "\t" + ClusterBenchmarkService.getClusterBenchmarkResult(clusterBenchmarkInputData).getBenchmarkValue());
        }
    }
}
