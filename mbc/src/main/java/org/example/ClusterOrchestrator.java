package org.example;

import Common.GeneExpressionData;
import FileDataOperations.GeneExpressionDataWrite;
import Interfaces.IClusterBenchmark;
import Interfaces.IClusterBenchmarkService;
import Interfaces.IClusterGenerationService;
import Interfaces.IClusterOrchestrator;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IDataProcessor;
import Interfaces.IGeneDistance;

public class ClusterOrchestrator implements IClusterOrchestrator {
    private final IDataProcessor dataProcessor;
    private final IClusterGenerationService generationService;
    private final IClusterBenchmarkService benchmarkService;

    public ClusterOrchestrator(IDataProcessor dataProcessor, IClusterGenerationService generationService, IClusterBenchmarkService benchmarkService) {
        this.dataProcessor = dataProcessor;
        this.generationService = generationService;
        this.benchmarkService = benchmarkService;
    }

    @Override
    public void executePipeline(GeneExpressionData rawData, String processedDataPath, IClusteringAlgorithm algorithm, IGeneDistance geneDistance, IClusterBenchmark benchmark, String outputFilePrefix) {
        // 1. Process data
        GeneExpressionData processedData = dataProcessor.processData(rawData);

        // 2. Export processed data to CSV
        new GeneExpressionDataWrite().writeGeneExpressionDataToFile(processedData, processedDataPath);

        // 3. Run clustering (this creates the file outputFilePrefix)
        generationService.runClustering(processedData, algorithm, outputFilePrefix);
        
        // 4. Run benchmarks (reading from both the processed data and clustering result files)
        // Note: ClusterGenerationService saves to outputFilePrefix as is.
        benchmarkService.runBenchmark(processedDataPath, outputFilePrefix + ".txt", geneDistance, benchmark, outputFilePrefix + "_benchmarks");
    }
}
