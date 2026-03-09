package ReplicateCompression;

import Interfaces.IReplicateCompression;

import java.util.ArrayList;

/**
 * Abstract base class for replicate compression strategies.
 */
public abstract class AReplicateCompression implements IReplicateCompression {
    
    @Override
    public double[][] compress(double[][] data, int[] replicatesPerTime, int numberOfTimeSeries) {
        int numberOfGenes = data.length;
        double[][] compressedData = new double[numberOfGenes][numberOfTimeSeries];

        for (int g = 0; g < numberOfGenes; g++) {
            int currentOffset = 0;
            for (int t = 0; t < numberOfTimeSeries; t++) {
                int nReplicates = replicatesPerTime[t];
                ArrayList<Double> replicates = new ArrayList<>();

                for (int i = 0; i < nReplicates; i++) {
                    replicates.add(data[g][currentOffset + i]);
                }

                compressedData[g][t] = calculate(replicates);
                currentOffset += nReplicates;
            }
        }
        return compressedData;
    }

    /**
     * Calculates the compressed value for a set of replicates.
     * 
     * @param replicates the list of replicate expression values
     * @return the compressed representative value
     */
    protected abstract double calculate(ArrayList<Double> replicates);
}
