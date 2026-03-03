package org.example;

import Common.GeneExpressionData;
import Interfaces.IClusterBenchmark;
import Interfaces.IClusterBenchmarkService;
import Interfaces.IClusterGenerationService;
import Interfaces.IClusterOrchestrator;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IDataProcessor;

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
    public void executePipeline(GeneExpressionData rawData, IClusteringAlgorithm algorithm, IClusterBenchmark benchmark, String outputFilePrefix) {
        GeneExpressionData processedData = dataProcessor.processData(rawData);
        generationService.runClustering(processedData, algorithm, outputFilePrefix);
        
        String finalPrefix = outputFilePrefix + ".txt";
        benchmarkService.runBenchmark(processedData, benchmark, finalPrefix);
    }
}
