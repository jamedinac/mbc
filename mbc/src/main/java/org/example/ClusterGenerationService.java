package org.example;

import ClusterWorkflow.ProfileClusterWorkflowDecorator;
import ClusterWorkflow.StandardClusterWorkflow;
import ClusteringAlgorithms.ClusterAlgorithmFactory;
import Common.DataProcessor;
import Common.WorkflowResult;
import Enum.ReplicateCompressionType;
import FileDataOperations.DataLoad;
import FileDataOperations.GeneClusterDataWrite;
import FileDataOperations.GeneExpressionDataWrite;
import Filter.CompositeFilter;
import Filter.GeneFilterByTotalExpression;
import Filter.GeneFilterByVariance;
import Filter.SampleFilter;
import Filter.ZeroFilter;
import GeneDistance.CorrelationDistance;
import Interfaces.ClusterWorkflow;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IDataLoad;
import Interfaces.IDataProcessor;
import Interfaces.IGeneClusterDataWrite;
import Interfaces.IGeneDistance;
import Interfaces.IReplicateCompression;
import Interfaces.ISampleFilter;
import Normalizers.CompositeNormalizer;
import Normalizers.IRLS;
import ReplicateCompression.ReplicateCompressionFactory;

public class ClusterGenerationService {

    private static final String outputFilePrefix = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\IR64\\output";
    private static final String geneExpressionFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\IR64\\complete_data.tsv";
    private static final String metadataFileName = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\IR64\\metadata.csv";
    private static final String processedDataPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\IR64\\processed_data.csv";

    private static final String timeSeriesColumn = "Time";
    private static final String sampleColumn = "Sample";

    private static final int numberOfClusters = 4;
    private static final int numberOfIterations = 1000;

    public static void main(String[] args) {
        // 1. Dependency Configuration
        IGeneDistance geneDistance = new CorrelationDistance();

        IDataLoad dataLoad = new DataLoad(
                geneExpressionFileName,
                metadataFileName,
                timeSeriesColumn,
                sampleColumn
        );

        CompositeFilter geneFilter = new CompositeFilter();
        geneFilter.addfilter(new ZeroFilter());
        geneFilter.addfilter(new GeneFilterByTotalExpression(1));
        geneFilter.addfilter(new GeneFilterByVariance(1));

        ISampleFilter sampleFilter = new SampleFilter();

        CompositeNormalizer normalizer = new CompositeNormalizer();
        normalizer.add(new IRLS());

        IReplicateCompression compression = ReplicateCompressionFactory.createReplicateCompression(ReplicateCompressionType.Default);

        IDataProcessor dataProcessor = new DataProcessor(geneFilter, sampleFilter, compression, normalizer);
        
        IClusteringAlgorithm algorithm = ClusterAlgorithmFactory.createKMeans(numberOfClusters, numberOfIterations, geneDistance);

        // 2. Workflow Assembly
        ClusterWorkflow baseWorkflow = new StandardClusterWorkflow(dataLoad, dataProcessor, algorithm);
        ClusterWorkflow profiledWorkflow = new ProfileClusterWorkflowDecorator(baseWorkflow);

        // 3. Execution (Time and Peak Memory tracked within the decorator)
        WorkflowResult result = profiledWorkflow.execute();

        // 4. Final I/O (Excluded from performance metrics)
        new GeneExpressionDataWrite().writeGeneExpressionDataToFile(result.getProcessedData(), processedDataPath);

        IGeneClusterDataWrite geneExpressionDataWrite = new GeneClusterDataWrite();
        geneExpressionDataWrite.writeClusteringDataToFile(result.getClusterData(), outputFilePrefix);
    }
}
