package ReplicateCompression;

import Interfaces.IReplicateCompression;
import Enum.ReplicateCompressionType;

public class ReplicateCompressionFactory {
    public static IReplicateCompression createReplicateCompression(ReplicateCompressionType compressionType){
        IReplicateCompression replicateCompression = null;

        switch (compressionType){
            case ReplicateCompressionType.Default ->  replicateCompression = new DefaultReplicateCompression();
            case ReplicateCompressionType.Variance ->   replicateCompression = new VarianceReplicateCompression();
            case ReplicateCompressionType.Mean -> replicateCompression = new MeanReplicateCompression();
        }

        return  replicateCompression;
    }

    public static IReplicateCompression createReplicateCompression(String type) {
        return switch (type.toLowerCase()) {
            case "mean" -> createReplicateCompression(ReplicateCompressionType.Mean);
            case "variance" -> createReplicateCompression(ReplicateCompressionType.Variance);
            default -> createReplicateCompression(ReplicateCompressionType.Default);
        };
    }
}
