package Common;

import Interfaces.*;
import Enum.FileFormat;
import Enum.ClusteringAlgorithmType;
import ReplicateCompression.DefaultReplicateCompression;

import java.util.ArrayList;

public class ClusterParameters {
    protected final String geneExpressionFileName;

    protected final String metadataFileName;

    protected final FileFormat geneExpressionFileFormat;

    protected final FileFormat metadataFileFormat;

    protected final String outputFileName;

    protected final ClusteringAlgorithmType algorithmType;

    protected IClusteringAlgorithm algorithm;

    protected ArrayList<IGeneFilter> geneFilters = new ArrayList<>();

    protected ArrayList<SampleTrait> sampleFilters = new ArrayList<>();

    protected ArrayList<IDataNormalizer> normalizers = new ArrayList<>();

    protected IReplicateCompression compression = new DefaultReplicateCompression();

    protected String replicateColumn = "Replicate";

    protected String timeSeriesColumn = "Time";

    protected String sampleColumn = "Sample";

    protected int numberOfReplicates = 3;

    protected int numberOfTimeSeries = 13;

    public ClusterParameters(
            String geneExpressionFileName,
            String metadataFileName,
            FileFormat geneExpressionFileFormat,
            FileFormat metadataFileFormat,
            String  outputFileName,
            ClusteringAlgorithmType algorithmType,
            IClusteringAlgorithm algorithm
    ) {
        this.geneExpressionFileName = geneExpressionFileName;
        this.metadataFileName = metadataFileName;
        this.geneExpressionFileFormat = geneExpressionFileFormat;
        this.metadataFileFormat = metadataFileFormat;
        this.outputFileName = outputFileName;
        this.algorithmType = algorithmType;
        this.algorithm = algorithm;
    }

    public String getGeneExpressionFileName() {
        return geneExpressionFileName;
    }

    public String getMetadataFileName() {
        return metadataFileName;
    }

    public FileFormat getGeneExpressionFileFormat() {
        return geneExpressionFileFormat;
    }

    public FileFormat getMetadataFileFormat() {
        return metadataFileFormat;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public ClusteringAlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    public IClusteringAlgorithm getAlgorithm() {
        return algorithm;
    }

    public ArrayList<IGeneFilter> getGeneFilters() {
        return geneFilters;
    }

    public ArrayList<SampleTrait> getSampleFilters() {
        return sampleFilters;
    }

    public ArrayList<IDataNormalizer> getNormalizers() {
        return normalizers;
    }

    public IReplicateCompression getCompression() {
        return compression;
    }

    public String getReplicateColumn() {
        return replicateColumn;
    }

    public String getTimeSeriesColumn() {
        return timeSeriesColumn;
    }

    public String getSampleColumn() {
        return sampleColumn;
    }

    public int getNumberOfReplicates() {
        return numberOfReplicates;
    }

    public int getNumberOfTimeSeries() {
        return numberOfTimeSeries;
    }

    public void setAlgorithm(IClusteringAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void setGeneFilters(ArrayList<IGeneFilter> geneFilters) {
        this.geneFilters = geneFilters;
    }

    public void setSampleFilters(ArrayList<SampleTrait> sampleFilters) {
        this.sampleFilters = sampleFilters;
    }

    public void setNormalizers(ArrayList<IDataNormalizer> normalizers) {
        this.normalizers = normalizers;
    }

    public void setCompression(IReplicateCompression compression) {
        this.compression = compression;
    }

    public void setReplicateColumn(String replicateColumn) {
        this.replicateColumn = replicateColumn;
    }

    public void setTimeSeriesColumn(String timeSeriesColumn) {
        this.timeSeriesColumn = timeSeriesColumn;
    }

    public void setSampleColumn(String sampleColumn) {
        this.sampleColumn = sampleColumn;
    }

    public void setNumberOfReplicates(int numberOfReplicates) {
        this.numberOfReplicates = numberOfReplicates;
    }

    public void setNumberOfTimeSeries(int numberOfTimeSeries) {
        this.numberOfTimeSeries = numberOfTimeSeries;
    }
}