package GeneExpressionDataSource;

import Common.GeneExpressionData;
import Interfaces.IDataGenerator;
import Interfaces.IGeneExpressionDataSource;

public class SimulatedGeneExpressionDatasource implements IGeneExpressionDataSource {

    private final IDataGenerator dataGenerator;
    private final int numberOfGenes;
    private final int numberOfSamples;
    private final int numberOfTimeSeries;

    public SimulatedGeneExpressionDatasource (IDataGenerator dataGenerator, int numberOfGenes, int numberOfSamples, int numberOfTimeSeries) {
        this.dataGenerator = dataGenerator;
        this.numberOfGenes = numberOfGenes;
        this.numberOfSamples = numberOfSamples;
        this.numberOfTimeSeries = numberOfTimeSeries;
    }

    public GeneExpressionData getGeneExpressionFormattedData () {
        int[][] geneExpressionData = new int[numberOfGenes][numberOfSamples * numberOfTimeSeries];

        for (int i = 0; i < numberOfGenes; i++) {
            for (int j = 0; j < numberOfSamples * numberOfTimeSeries; j++) {
                geneExpressionData[i][j] = dataGenerator.generateRandomNumber();
            }
        }

        return new GeneExpressionData(numberOfGenes, numberOfSamples, numberOfTimeSeries, geneExpressionData);
    }
}
