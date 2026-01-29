package GeneExpressionDataSource;

import Common.GeneExpressionData;
import Interfaces.IDataGenerator;
import Interfaces.IGeneExpressionDataSource;

public class SimulatedGeneExpressionDatasource implements IGeneExpressionDataSource {

    private final IDataGenerator dataGenerator;
    private final int numberOfGenes;
    private final int numberOfReplicates;
    private final int numberOfTimeSeries;

    public SimulatedGeneExpressionDatasource (IDataGenerator dataGenerator, int numberOfGenes, int numberOfSamples, int numberOfTimeReplicates) {
        this.dataGenerator = dataGenerator;
        this.numberOfGenes = numberOfGenes;
        this.numberOfReplicates = numberOfSamples;
        this.numberOfTimeSeries = numberOfTimeReplicates;
    }

    public GeneExpressionData getGeneExpressionFormattedData () {
        int numberOfComponents = this.numberOfGenes * this.numberOfReplicates;

        double[][] geneExpressionData = new double[numberOfGenes][numberOfComponents];
        String[] metadata = new String[numberOfComponents];
        String[] geneId = new String[numberOfGenes];

        for (int i = 0; i < numberOfGenes; i++) {
            geneId[i] = "gene" + i;
            for (int j = 0; j < numberOfReplicates * numberOfTimeSeries; j++) {
                geneExpressionData[i][j] = dataGenerator.generateRandomDouble();
                metadata[j] = Integer.toString(j / numberOfTimeSeries);
            }
        }

        return new GeneExpressionData(numberOfGenes, numberOfReplicates, numberOfTimeSeries, geneExpressionData, metadata, geneId);
    }
}
