package ReplicateCompression;

import Interfaces.IReplicateCompression;

public class DefaultReplicateCompression implements IReplicateCompression {
    @Override
    public double[][] compress(double[][] data, int numberOfReplicates, int numberOfTimeSeries) {
        return data;
    }
}
