package ReplicateCompression;

import java.util.ArrayList;

public class MeanReplicateCompression extends AReplicateCompression {
    protected double calculate(ArrayList<Double> replicates) {
        double mean = 0.0;
        for (Double replicate : replicates) {
            mean += replicate;
        }
        return mean / replicates.size();
    }
}
