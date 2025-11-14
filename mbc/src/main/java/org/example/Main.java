package org.example;

import ClusteringAlgorithms.DummyAlgorithm;
import Common.GeneClusteringData;
import Common.GeneExpressionData;
import DataGenerators.UniformDataGenerator;
import GeneExpressionDataSource.SimulatedGeneExpressionDatasource;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IDataGenerator;
import Interfaces.IGeneExpressionDataSource;

public class Main {
    static void main() {
        int uniformDataGeneratorLimit = 100;
        IDataGenerator uniformDataGenerator = new UniformDataGenerator(uniformDataGeneratorLimit);

        int numberOfGenes = 10;
        int numberOfSamples = 3;
        int numberOfTimSeries = 5;

        IGeneExpressionDataSource simulatedGeneExpressionDatasource = new SimulatedGeneExpressionDatasource(
                uniformDataGenerator,
                numberOfGenes,
                numberOfSamples,
                numberOfTimSeries);

        GeneExpressionData geneExpressionData = simulatedGeneExpressionDatasource.getGeneExpressionFormattedData();

        for (int i = 0; i < geneExpressionData.getNumberOfGenes(); i++) {
            for (int j = 0; j < geneExpressionData.getNumberOfSamples() * geneExpressionData.getNumberOfTimeSeries(); j++) {
                System.out.print(geneExpressionData.getExpressionData()[i][j] + " ");
            }
            System.out.println();
        }

        IClusteringAlgorithm dummyAlgorithm = new DummyAlgorithm();
        GeneClusteringData geneClusteringData = dummyAlgorithm.clusterGenes(geneExpressionData);

        for (int i = 0; i < geneClusteringData.getNumberOfGenes(); i++) {
            for (int j = 0; j < geneClusteringData.getNumberOfClusters(); j++) {
                System.out.print(geneClusteringData.getGeneClusteringData()[i][j] + " ");
            }
            System.out.println();
        }
    }
}
