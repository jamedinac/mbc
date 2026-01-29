package GeneExpressionDataSource;

import Common.GeneExpressionData;
import GeneProfile.IntegerGeneProfile;
import Interfaces.IDataGenerator;
import Interfaces.IGeneExpressionDataSource;

import java.util.ArrayList;

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
        ArrayList<IntegerGeneProfile> geneExpressionData = new ArrayList<>(numberOfGenes);
        ArrayList<String> metadata = new ArrayList<>(numberOfReplicates * numberOfTimeSeries);

        for (int i = 0; i < numberOfGenes; i++) {
            ArrayList<Integer> geneProfile = new ArrayList<>(numberOfReplicates * numberOfTimeSeries);

            for (int j = 0; j < numberOfReplicates * numberOfTimeSeries; j++) {
                geneProfile.set(j, dataGenerator.generateRandomInteger());
                metadata.set(j, Integer.toString(j / numberOfTimeSeries));
            }

            geneExpressionData.add(new IntegerGeneProfile(geneProfile, Integer.toString(i)));
        }

        return new GeneExpressionData(numberOfGenes, numberOfReplicates, numberOfTimeSeries, geneExpressionData, metadata);
    }
}
