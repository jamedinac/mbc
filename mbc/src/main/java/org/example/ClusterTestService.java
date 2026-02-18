package org.example;

import ClusterBenchmark.ClusterBenchmarkFactory;
import ClusteringAlgorithms.ClusterAlgorithmFactory;
import Enum.BenchmarkType;
import Enum.FileFormat;
import Enum.ReplicateCompressionType;
import Enum.ClusteringAlgorithmType;
import Common.SampleTrait;
import Filter.GeneFilterByTotalExpression;
import Filter.GeneFilterByVariance;
import Filter.ZeroFilter;
import GeneDistance.EuclideanDistance;
import GeneDistance.JensenShannonDistance;
import Interfaces.*;
import Normalizers.EnthropyNormalizer;
import ReplicateCompression.ReplicateCompressionFactory;

import java.util.ArrayList;

public class ClusterTestService {
    static void main() {
        String directoryPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated";
        String fileName = "output.txt";
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
        normalizers.add(new EnthropyNormalizer());

        IGeneDistance geneDistance = new JensenShannonDistance();

        ClusteringAlgorithmType algorithmType = ClusteringAlgorithmType.KMeans;
        IClusteringAlgorithm algorithm = ClusterAlgorithmFactory.getClusteringAlgorithm(algorithmType, numberOfClusters, numberOfIterations, geneDistance);

        BenchmarkType benchmarkType = BenchmarkType.Silhouette;
        IClusterBenchmark benchmark = ClusterBenchmarkFactory.create(benchmarkType, geneDistance, null);

        ReplicateCompressionType replicateCompression = ReplicateCompressionType.Variance;
        IReplicateCompression compression = ReplicateCompressionFactory.createReplicateCompression(replicateCompression);

        ClusterGenerationService.RunClustering(directoryPath, geneExpressionFileName, metadataFileName, geneFilters, sampleFilters, normalizers, algorithm, compression, replicateColumn, timeSeriesColumn, sampleColumn, numberOfReplicates, numberOfTimeSeries, geneExpressionFileFormat, metadataFileFormat);
        ClusterBenchmarkService.RunBenchmark(directoryPath, geneExpressionFileName, metadataFileName, fileName, benchmark, geneFilters, sampleFilters, normalizers, compression, replicateColumn, timeSeriesColumn, sampleColumn, numberOfReplicates, numberOfTimeSeries, geneExpressionFileFormat, metadataFileFormat);
    }

}
