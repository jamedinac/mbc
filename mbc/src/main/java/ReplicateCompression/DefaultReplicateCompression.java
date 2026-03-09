package ReplicateCompression;

import Interfaces.IReplicateCompression;

/**
 * Implementation that returns the data as-is without any compression.
 */
public class DefaultReplicateCompression implements IReplicateCompression {
    
    @Override
    public double[][] compress(double[][] data, int[] replicatesPerTime, int numberOfTimeSeries) {
        return data;
    }
}
