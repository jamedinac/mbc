package ReplicateCompression;

import Interfaces.IReplicateCompression;

import java.util.ArrayList;

public abstract class AReplicateCompression implements IReplicateCompression {
    @Override
    public double[][] compress(double[][] data, int numberOfReplicates, int numberOfTimeSeries) {
        int numberOfGenes = data.length;

        double[][] compressedData = new  double[numberOfGenes][numberOfTimeSeries];

        for (int g=0;g<numberOfGenes;g++){
            for (int c=0; c<numberOfTimeSeries*numberOfReplicates; c += numberOfReplicates){
                ArrayList<Double> replicates = new  ArrayList<>();

                for (int i=0;i<numberOfReplicates;i++){
                    replicates.add(data[g][c + i]);
                }

                compressedData[g][c/numberOfReplicates] = calculate(replicates);
            }
        }
        return compressedData;
    }

    protected abstract double calculate(ArrayList<Double> replicates);
}
