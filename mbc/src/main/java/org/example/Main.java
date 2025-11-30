package org.example;

import ClusterBenchmark.JackardBenchmark;
import ClusteringAlgorithms.DummyAlgorithm;
import Common.ClusterResult;
import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import DataGenerators.UniformDataGenerator;
import GeneExpressionDataSource.SimulatedGeneExpressionDatasource;
import Interfaces.IClusterBenchmark;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IDataGenerator;
import Interfaces.IGeneExpressionDataSource;

public class Main {
    static void main() {
        int uniformDataGeneratorLimit = 100;
        IDataGenerator uniformDataGenerator = new UniformDataGenerator(uniformDataGeneratorLimit);

        int numberOfGenes = 10;
        int numberOfReplicates = 3; //TODO: replicates
        int numberOfTimSeries = 5;

        IGeneExpressionDataSource simulatedGeneExpressionDatasource = new SimulatedGeneExpressionDatasource(
                uniformDataGenerator,
                numberOfGenes,
                numberOfReplicates,
                numberOfTimSeries);

        GeneExpressionData geneExpressionData = simulatedGeneExpressionDatasource.getGeneExpressionFormattedData();

        IClusteringAlgorithm dummyAlgorithm = new DummyAlgorithm();
        GeneClusteringResult geneClusteringData = dummyAlgorithm.clusterGenes(geneExpressionData);

        /*
         * TODO separar benchmarking del algoritmo
         */
        IClusterBenchmark jackardEvaluation = new JackardBenchmark();
        ClusterResult clusterResult = jackardEvaluation.evaluate(geneClusteringData, geneExpressionData);
    }
}
