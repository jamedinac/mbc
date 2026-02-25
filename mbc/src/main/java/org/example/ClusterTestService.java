package org.example;

import ClusterBenchmark.ClusterBenchmarkFactory;
import ClusteringAlgorithms.ClusterAlgorithmFactory;
import Common.*;
import Enum.BenchmarkType;
import Enum.FileFormat;
import Enum.ReplicateCompressionType;
import Enum.ClusteringAlgorithmType;
import FileDataOperations.GeneClusterDataLoad;
import Filter.GeneFilterByTotalExpression;
import Filter.GeneFilterByVariance;
import Filter.ZeroFilter;
import GeneDistance.EuclideanDistance;
import GeneDistance.JensenShannonDistance;
import Interfaces.*;
import LinkageCriteria.AverageLinkage;
import Normalizers.CountDistributionNormalizer;
import Normalizers.EmpiricalBayesShrinkageEstimationNormalizer;
import Normalizers.MedianRatiosNormalization;
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

        /// TODO: Set number of clusters and iterations
        int numberOfClusters = 4;
        int numberOfIterations = 50;

        ///  TODO: Set distance definition
        IGeneDistance geneDistance = new JensenShannonDistance();

        ///  TODO: Set Cluster algorithm
        ClusteringAlgorithmType algorithmType = ClusteringAlgorithmType.Hierarchical;
        IClusteringAlgorithm algorithm = ClusterAlgorithmFactory.createHierarchical(numberOfClusters, geneDistance, new AverageLinkage());

        ClusterParameters clusterGenerationParameters = new ClusterParameters(geneExpressionFileName, metadataFileName, geneExpressionFileFormat, metadataFileFormat, outputFilePrefix, algorithmType, algorithm);

        ///  TODO: Set gene filters
        ArrayList<IGeneFilter> geneFilters = new ArrayList<>();
        geneFilters.add(new ZeroFilter());
        geneFilters.add(new GeneFilterByTotalExpression(1));
        geneFilters.add(new GeneFilterByVariance(1));
        clusterGenerationParameters.setGeneFilters(geneFilters);

        /// TODO: Set sample filters
        ArrayList<SampleTrait>  sampleFilters = new ArrayList<>();
        clusterGenerationParameters.setSampleFilters(sampleFilters);

        /// TODO Set normalizers
        ArrayList<IDataNormalizer> normalizers = new ArrayList<>();
        normalizers.add(new EmpiricalBayesShrinkageEstimationNormalizer(clusterGenerationParameters.getNumberOfReplicates(), clusterGenerationParameters.getNumberOfTimeSeries(), 1e-9));
        clusterGenerationParameters.setNormalizers(normalizers);

        /// TODO: Set compression type
        ReplicateCompressionType replicateCompression = ReplicateCompressionType.Default;
        IReplicateCompression compression = ReplicateCompressionFactory.createReplicateCompression(replicateCompression);
        clusterGenerationParameters.setCompression(compression);

        /// TODO: Set benchmark
        BenchmarkType benchmarkType = BenchmarkType.NMI;
        String goldStandardFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\ground_truth.txt";
        GeneClusterData goldStandard = new GeneClusterDataLoad(goldStandardFileName).readClusterData();
        IClusterBenchmark benchmark = ClusterBenchmarkFactory.create(benchmarkType, geneDistance, goldStandard);

        int startCluster = 1;
        int endCluster = 10;

        //RunSeveralClustersAttempt(clusterGenerationParameters, startCluster, endCluster, numberOfIterations, algorithmType, geneDistance, benchmark);
        RunSingleClusterAttempt(clusterGenerationParameters, benchmark);
    }

    private static void RunSingleClusterAttempt(ClusterParameters clusterParameters, IClusterBenchmark benchmark) {
        ClusterGenerationService.RunClustering(clusterParameters);
        clusterParameters.setOutputFilePrefix(clusterParameters.getOutputFilePrefix() + ".txt");
        ClusterBenchmarkService.RunBenchmark(clusterParameters, benchmark);
    }

    private static void RunSeveralClustersAttempt(ClusterParameters clusterParameters, int startCluster, int endCluster, int numberOfIterations, ClusteringAlgorithmType algorithmType, IGeneDistance geneDistance, IClusterBenchmark benchmark) {
        clusterParameters.setOutputFilePrefix(clusterParameters.getOutputFilePrefix() + ".txt");

        for (int c = startCluster; c <= endCluster; c++) {
            clusterParameters.setAlgorithm(ClusterAlgorithmFactory.createKMeans(c, numberOfIterations, geneDistance));
            ClusterGenerationService.RunClustering(clusterParameters);
            System.out.println(c + "\t" + ClusterBenchmarkService.getClusterBenchmarkResult(clusterParameters, benchmark).getBenchmarkValue());
        }
    }
}
