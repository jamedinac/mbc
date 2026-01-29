package Common;

import GeneProfile.AGeneProfile;
import GeneProfile.DoubleGeneProfile;
import GeneProfile.IntegerGeneProfile;

import java.util.ArrayList;

public class GeneExpressionData {

    private final int numberOfGenes;
    private final int numberOfReplicates;
    private final int numberOfTimeSeries;
    private final ArrayList<IntegerGeneProfile> expressionData;
    private final ArrayList<String> metadata;

    public GeneExpressionData(int numberOfGenes, int numberOfReplicates, int numberOfTimeSeries, ArrayList<IntegerGeneProfile> expressionData, ArrayList<String> metadata) {
        this.numberOfGenes = numberOfGenes;
        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;
        this.expressionData = expressionData;
        this.metadata = metadata;
    }

    public ArrayList<IntegerGeneProfile> getExpressionData() {
        return expressionData;
    }

    public int getNumberOfGenes() {
        return numberOfGenes;
    }

    public int getNumberOfReplicates() {
        return numberOfReplicates;
    }

    public int getNumberOfTimeSeries() {
        return numberOfTimeSeries;
    }

    public ArrayList<String> getMetadata() {
        return metadata;
    }

    public IntegerGeneProfile getGeneProfile(int geneIndex){
        return expressionData.get(geneIndex);
    }
}
