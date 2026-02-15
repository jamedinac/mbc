package ReplicateCompression;

import Interfaces.IReplicateCompression;

import java.util.ArrayList;

public class VarianceReplicateCompression extends AReplicateCompression implements IReplicateCompression {
    protected double calculate(ArrayList<Double> replicates){
        double mean = this.getMean(replicates);
        double variance = 0.0;

        for (Double replicate : replicates){
            variance += (replicate - mean) * (replicate - mean);
        }

        return variance / replicates.size();
    }

    private double getMean(ArrayList<Double> replicates){
        double mean = 0.0;
        for (Double replicate : replicates){
            mean += replicate;
        }
        return mean / replicates.size();
    }
}
