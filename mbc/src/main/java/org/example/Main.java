package org.example;

import DataGenerators.UniformDataGenerator;
import GeneExpressionDataSource.SimulatedGeneExpressionDatasource;
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

        int[][] geneExpressionData = simulatedGeneExpressionDatasource.getGeneExpressionFormattedData();

        for (int i = 0; i < geneExpressionData.length; i++) {
            for (int j = 0; j < geneExpressionData[i].length; j++) {
                System.out.print(geneExpressionData[i][j] + " ");
            }
            System.out.println();
        }
    }
}
