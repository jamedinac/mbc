package org.example;

import ClusterBenchmark.ClusterBenchmarkFactory;
import ClusteringAlgorithms.ClusterAlgorithmFactory;
import Enum.BenchmarkType;
import Enum.ReplicateCompressionType;
import Common.ClusterBenchmarkResult;
import Enum.ClusteringAlgorithmType;
import Common.SampleTrait;
import Filter.GeneFilterByTotalExpression;
import Filter.GeneFilterByVariance;
import Filter.ZeroFilter;
import GeneDistance.EuclideanDistance;
import GeneDistance.JensenShannonDistance;
import Interfaces.*;
import Normalizers.EnthropyNormalizer;
import Normalizers.MedianRatiosNormalization;
import ReplicateCompression.ReplicateCompressionFactory;

import java.util.ArrayList;

public class ClusterTestService {
    static void main() {
        String directoryPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\IR64";
        String fileName = "output.txt";
        String geneExpressionFileName = "data.txt";
        String metadataFileName = "metadata.txt";

        int numberOfClusters = 10;
        int numberOfIterations = 20;

        ArrayList<IGeneFilter> geneFilters = new ArrayList<>();
        geneFilters.add(new ZeroFilter());
        geneFilters.add(new GeneFilterByTotalExpression(1));
        geneFilters.add(new GeneFilterByVariance(1));

        ArrayList<SampleTrait>  sampleFilters = new ArrayList<>();
        sampleFilters.add(new SampleTrait("Drought_Group", "Severe"));
        sampleFilters.add(new SampleTrait("Condition", "Drought"));

        ArrayList<IDataNormalizer> normalizers = new ArrayList<>();
        normalizers.add(new EnthropyNormalizer());
        //normalizers.add(new MedianRatiosNormalization());
        //normalizers.add(new PseudologarithmNormalizer());
        //normalizers.add(new ZScoreNormalizer());

        IGeneDistance geneDistance = new JensenShannonDistance();

        ClusteringAlgorithmType algorithmType = ClusteringAlgorithmType.KMeans;
        IClusteringAlgorithm algorithm = ClusterAlgorithmFactory.getClusteringAlgorithm(algorithmType, numberOfClusters, numberOfIterations, geneDistance);

        BenchmarkType benchmarkType = BenchmarkType.WCSS;
        IClusterBenchmark benchmark = ClusterBenchmarkFactory.create(benchmarkType, new EuclideanDistance(), null);

        ReplicateCompressionType replicateCompression = ReplicateCompressionType.Variance;
        IReplicateCompression compression = ReplicateCompressionFactory.createReplicateCompression(replicateCompression);

        //ClusterGenerationService.RunClustering(directoryPath, geneExpressionFileName, metadataFileName, numberOfClusters, numberOfIterations, geneFilters, sampleFilters, normalizers, algorithm, compression);
        //ClusterBenchmarkService.RunBenchmark(directoryPath, geneExpressionFileName, metadataFileName, fileName, benchmark, geneFilters, sampleFilters, normalizers, compression);

        for (int c=1; c<=numberOfClusters; c++) {
            ClusterGenerationService.RunClustering(directoryPath, geneExpressionFileName, metadataFileName, c, numberOfIterations, geneFilters, sampleFilters, normalizers, algorithm, compression);
            ClusterBenchmarkResult clusterBenchmarkResult = ClusterBenchmarkService.getClusterBenchmarkResult(directoryPath, geneExpressionFileName, metadataFileName, fileName, benchmark, geneFilters, sampleFilters, normalizers, compression);

            double value = clusterBenchmarkResult.getBenchmarkValue();
            System.out.println(c + "\t" + String.format("%.6f", value));
        }
    }

}
