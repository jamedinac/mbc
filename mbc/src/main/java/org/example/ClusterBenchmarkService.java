package org.example;

import ClusterBenchmark.Silhouette;
import ClusteringAlgorithms.KMeansAlgorithm;
import Common.ClusterBenchmarkResult;
import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import Interfaces.IClusterBenchmark;
import Interfaces.IClusteringAlgorithm;

public class ClusterBenchmarkService {
    static void main() {
        String directoryPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\IR64";
        GeneExpressionData geneExpressionData = ClusterGenerationService.getGeneExpressionData(directoryPath);

        IClusteringAlgorithm kMeans = new KMeansAlgorithm(10, 20);
        GeneClusteringResult kMeansResult = kMeans.clusterGenes(geneExpressionData);

        IClusterBenchmark silhouetteClustering = new Silhouette(kMeansResult);
        ClusterBenchmarkResult clusterBenchmarkResult = silhouetteClustering.evaluate();

        clusterBenchmarkResult.writeClusterBenchmarkToFile(directoryPath);
    }
}
