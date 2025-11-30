package org.example;

import ClusteringAlgorithms.DummyAlgorithm;
import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import DataGenerators.UniformDataGenerator;
import GeneExpressionDataSource.SimulatedGeneExpressionDatasource;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IDataGenerator;
import Interfaces.IGeneExpressionDataSource;

public class ClusterGenerationService {
    static void main() {
        int uniformDataGeneratorLimit = 100;
        IDataGenerator uniformDataGenerator = new UniformDataGenerator(uniformDataGeneratorLimit);

        int numberOfGenes = 10;
        int numberOfReplicates = 3;
        int numberOfTimSeries = 5;

        IGeneExpressionDataSource simulatedGeneExpressionDatasource = new SimulatedGeneExpressionDatasource(
                uniformDataGenerator,
                numberOfGenes,
                numberOfReplicates,
                numberOfTimSeries);

        GeneExpressionData geneExpressionData = simulatedGeneExpressionDatasource.getGeneExpressionFormattedData();

        IClusteringAlgorithm dummyAlgorithm = new DummyAlgorithm();
        GeneClusteringResult geneClusteringData = dummyAlgorithm.clusterGenes(geneExpressionData);
    }
}
