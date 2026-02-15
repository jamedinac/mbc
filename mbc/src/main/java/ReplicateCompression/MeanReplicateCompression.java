package ReplicateCompression;

import Interfaces.IReplicateCompression;

import java.util.ArrayList;

public class MeanReplicateCompression extends AReplicateCompression implements IReplicateCompression{
    protected double calculate(ArrayList<Double> replicates){
        double mean = 0.0;
        for (Double replicate : replicates){
            mean += replicate;
        }
        return mean/replicates.size();
    }
}
