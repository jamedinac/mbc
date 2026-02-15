package org.example;

import ClusterBenchmark.ClusterBenchmarkFactory;
import ClusteringAlgorithms.ClusterAlgorithmFactory;
import Common.BenchmarkType;
import Common.ClusterBenchmarkResult;
import Common.ClusteringAlgorithmType;
import Common.SampleTrait;
import Filter.GeneFilterByTotalExpression;
import Filter.GeneFilterByVariance;
import GeneDistance.EuclideanDistance;
import Interfaces.*;
import Normalizers.MedianRatiosNormalization;
import Normalizers.PseudologarithmNormalizer;
import Normalizers.ZScoreNormalizer;

import java.util.ArrayList;

public class ClusterTestService {
    static void main() {
        String directoryPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\IR64";
        String fileName = "output.txt";
        String geneExpressionFileName = "data.txt";
        String metadataFileName = "metadata.txt";

        int numberOfClusters = 20;
        int numberOfIterations = 20;

        ArrayList<IGeneFilter> geneFilters = new ArrayList<>();
        geneFilters.add(new GeneFilterByTotalExpression(1));
        geneFilters.add(new GeneFilterByVariance(1));

        ArrayList<SampleTrait>  sampleFilters = new ArrayList<>();
        sampleFilters.add(new SampleTrait("Drought_Group", "Severe"));
        sampleFilters.add(new SampleTrait("Condition", "Drought"));

        ArrayList<IDataNormalizer> normalizers = new ArrayList<>();
        normalizers.add(new MedianRatiosNormalization());
        //normalizers.add(new PseudologarithmNormalizer());
        //normalizers.add(new ZScoreNormalizer());

        IGeneDistance geneDistance = new EuclideanDistance();

        ClusteringAlgorithmType algorithmType = ClusteringAlgorithmType.KMeans;
        IClusteringAlgorithm algorithm = ClusterAlgorithmFactory.getClusteringAlgorithm(algorithmType, numberOfClusters, numberOfIterations, geneDistance);

        BenchmarkType benchmarkType = BenchmarkType.Silhouette;
        IClusterBenchmark benchmark = ClusterBenchmarkFactory.create(benchmarkType, geneDistance, null);

        for (int c=1; c<=numberOfClusters; c++) {
            ClusterGenerationService.RunClustering(directoryPath, geneExpressionFileName, metadataFileName, c, numberOfIterations, geneFilters, sampleFilters, normalizers, algorithm);
            ClusterBenchmarkResult clusterBenchmarkResult = ClusterBenchmarkService.getClusterBenchmarkResult(directoryPath, geneExpressionFileName, metadataFileName, fileName, benchmark, geneFilters, sampleFilters, normalizers);

            double value = clusterBenchmarkResult.getBenchmarkValue();
            System.out.println(c + "\t" + String.format("%.6f", value));
        }

    }
}
