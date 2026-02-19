package Common;

import Interfaces.*;
import Enum.FileFormat;

import java.util.ArrayList;

public class ClusterBenchmarkInputData extends ClusterGenerationInputData {

    private final String outputFileName;

    private final IClusterBenchmark benchmark;

    public ClusterBenchmarkInputData(
            String directoryPath,
            String geneExpressionFileName,
            String metadataFileName,
            FileFormat geneExpressionFileFormat,
            FileFormat metadataFileFormat,
            String replicateColumn,
            String timeSeriesColumn,
            String sampleColumn,
            int numberOfReplicates,
            int numberOfTimeSeries,
            IReplicateCompression compression,
            ArrayList<IGeneFilter> geneFilters,
            ArrayList<SampleTrait> sampleFilters,
            ArrayList<IDataNormalizer> normalizers,
            String outputFileName,
            IClusterBenchmark benchmark
    ) {
        super(
                directoryPath,
                geneExpressionFileName,
                metadataFileName,
                geneExpressionFileFormat,
                metadataFileFormat,
                replicateColumn,
                timeSeriesColumn,
                sampleColumn,
                numberOfReplicates,
                numberOfTimeSeries,
                compression,
                geneFilters,
                sampleFilters,
                normalizers,
                null,
                null
        );
        this.outputFileName = outputFileName;
        this.benchmark = benchmark;
    }

    public String getOutputFileName() { return outputFileName; }

    public IClusterBenchmark getBenchmark() { return benchmark; }

    public ClusterGenerationInputData getClusterGenerationInputData() {
        return new ClusterGenerationInputData(
                getDirectoryPath(),
                getGeneExpressionFileName(),
                getMetadataFileName(),
                getGeneExpressionFileFormat(),
                getMetadataFileFormat(),
                getReplicateColumn(),
                getTimeSeriesColumn(),
                getSampleColumn(),
                getNumberOfReplicates(),
                getNumberOfTimeSeries(),
                getCompression(),
                getGeneFilters(),
                getSampleFilters(),
                getNormalizers(),
                getClusteringAlgorithmType(),
                getAlgorithm()
        );
    }

}