package org.example;

import ClusterEvaluators.JackardEvaluation;
import ClusteringAlgorithms.DummyAlgorithm;
import Common.ClusterResult;
import Common.GeneClusteringData;
import Common.GeneExpressionData;
import DataGenerators.UniformDataGenerator;
import GeneExpressionDataSource.SimulatedGeneExpressionDatasource;
import Interfaces.IClusterEvaluation;
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

        IClusteringAlgorithm dummyAlgorithm = new DummyAlgorithm();
        GeneClusteringData geneClusteringData = dummyAlgorithm.clusterGenes(geneExpressionData);

        IClusterEvaluation jackardEvaluation = new JackardEvaluation();
        ClusterResult clusterResult = jackardEvaluation.evaluate(geneClusteringData, geneExpressionData);
    }
}
