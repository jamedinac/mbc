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
import GeneDistance.EuclideanDistance;
import Interfaces.*;
import Normalizers.MedianRatiosNormalization;
import ReplicateCompression.ReplicateCompressionFactory;

import java.util.ArrayList;

public class ClusterTestService {
    static void main() {
        String outputFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\output.txt";
        String geneExpressionFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\data.csv";
        String metadataFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\metadata.csv";

        FileFormat geneExpressionFileFormat = FileFormat.CSV;
        FileFormat metadataFileFormat = FileFormat.CSV;

        int numberOfClusters = 4;
        int numberOfIterations = 50;

        IGeneDistance geneDistance = new EuclideanDistance();

        ClusteringAlgorithmType algorithmType = ClusteringAlgorithmType.KMeans;
        IClusteringAlgorithm algorithm = ClusterAlgorithmFactory.getClusteringAlgorithm(algorithmType, numberOfClusters, numberOfIterations, geneDistance);

        ClusterParameters clusterGenerationParameters = new ClusterParameters(geneExpressionFileName, metadataFileName, geneExpressionFileFormat, metadataFileFormat, outputFileName, algorithmType, algorithm);

        ArrayList<IGeneFilter> geneFilters = new ArrayList<>();
        geneFilters.add(new ZeroFilter());
        geneFilters.add(new GeneFilterByTotalExpression(1));
        geneFilters.add(new GeneFilterByVariance(1));
        clusterGenerationParameters.setGeneFilters(geneFilters);

        ArrayList<SampleTrait>  sampleFilters = new ArrayList<>();
        clusterGenerationParameters.setSampleFilters(sampleFilters);

        ArrayList<IDataNormalizer> normalizers = new ArrayList<>();
        normalizers.add(new MedianRatiosNormalization());
        clusterGenerationParameters.setNormalizers(normalizers);

        ReplicateCompressionType replicateCompression = ReplicateCompressionType.Default;
        IReplicateCompression compression = ReplicateCompressionFactory.createReplicateCompression(replicateCompression);
        clusterGenerationParameters.setCompression(compression);

        BenchmarkType benchmarkType = BenchmarkType.Silhouette;
        IClusterBenchmark benchmark = ClusterBenchmarkFactory.create(benchmarkType, geneDistance, null);


        //RunSeveralClustersAttempt(clusterGenerationInputData, clusterBenchmarkInputData, 1, 20, numberOfIterations, algorithmType, geneDistance);
        RunSingleClusterAttempt(clusterGenerationParameters);
    }

    private static void RunSingleClusterAttempt(ClusterParameters clusterParameters) {
        ClusterGenerationService.RunClustering(clusterParameters);
        ClusterBenchmarkService.RunBenchmark(clusterParameters);
    }

    private static void RunSeveralClustersAttempt(ClusterParameters clusterParameters, int startCluster, int endCluster, int numberOfIterations, ClusteringAlgorithmType algorithmType, IGeneDistance geneDistance) {
        for (int c = startCluster; c <= endCluster; c++) {
            clusterParameters.setAlgorithm(ClusterAlgorithmFactory.getClusteringAlgorithm(algorithmType, c, numberOfIterations, geneDistance));

            ClusterGenerationService.RunClustering(clusterParameters);
            System.out.println(c + "\t" + ClusterBenchmarkService.getClusterBenchmarkResult(clusterParameters).getBenchmarkValue());
        }
    }
}
