package Common;

import Interfaces.*;
import Enum.FileFormat;
import Enum.ClusteringAlgorithmType;

import java.util.ArrayList;

public class ClusterGenerationInputData {

    private final String directoryPath;

    private final String geneExpressionFileName;

    private final String metadataFileName;

    private final ArrayList<IGeneFilter> geneFilters;

    private final ArrayList<SampleTrait> sampleFilters;

    private final ArrayList<IDataNormalizer> normalizers;

    private IClusteringAlgorithm algorithm;

    private final IReplicateCompression compression;

    private final String replicateColumn;

    private final String timeSeriesColumn;

    private final String sampleColumn;

    private final int numberOfReplicates;

    private final int numberOfTimeSeries;

    private final FileFormat geneExpressionFileFormat;

    private final FileFormat metadataFileFormat;

    private final ClusteringAlgorithmType clusteringAlgorithmType;

    public ClusterGenerationInputData(
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
            ClusteringAlgorithmType algorithmType,
            IClusteringAlgorithm algorithm
    ) {
        this.directoryPath = directoryPath;
        this.geneExpressionFileName = geneExpressionFileName;
        this.metadataFileName = metadataFileName;
        this.geneFilters = geneFilters;
        this.sampleFilters = sampleFilters;
        this.normalizers = normalizers;
        this.algorithm = algorithm;
        this.clusteringAlgorithmType = algorithmType;
        this.compression = compression;
        this.replicateColumn = replicateColumn;
        this.timeSeriesColumn = timeSeriesColumn;
        this.sampleColumn = sampleColumn;
        this.numberOfReplicates = numberOfReplicates;
        this.numberOfTimeSeries = numberOfTimeSeries;
        this.geneExpressionFileFormat = geneExpressionFileFormat;
        this.metadataFileFormat = metadataFileFormat;
    }

    public String getDirectoryPath() { return directoryPath; }

    public String getGeneExpressionFileName() { return geneExpressionFileName; }

    public String getMetadataFileName() { return metadataFileName; }

    public ArrayList<IGeneFilter> getGeneFilters() { return geneFilters; }

    public ArrayList<SampleTrait> getSampleFilters() { return sampleFilters; }

    public ArrayList<IDataNormalizer> getNormalizers() { return normalizers; }

    public IClusteringAlgorithm getAlgorithm() { return algorithm; }

    public void setAlgorithm(IClusteringAlgorithm algorithm) { this.algorithm = algorithm; }

    public IReplicateCompression getCompression() { return compression; }

    public String getReplicateColumn() { return replicateColumn; }

    public String getTimeSeriesColumn() { return timeSeriesColumn; }

    public String getSampleColumn() { return sampleColumn; }

    public int getNumberOfReplicates() { return numberOfReplicates; }

    public int getNumberOfTimeSeries() { return numberOfTimeSeries; }

    public FileFormat getGeneExpressionFileFormat() { return geneExpressionFileFormat; }

    public FileFormat getMetadataFileFormat() { return metadataFileFormat; }

    public ClusteringAlgorithmType getClusteringAlgorithmType() { return clusteringAlgorithmType; }

}