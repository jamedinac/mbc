package Interfaces;

public interface IClusterBenchmarkService {
    void runBenchmark(String processedDataFilePath, String clusterDataFilePath, IGeneDistance geneDistance, IClusterBenchmark clusterBenchmark, String outputFilePrefix);
}
