package org.example;

import ClusteringAlgorithms.KMeansAlgorithm;
import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import GeneExpressionDataOperation.GeneExpressionDataLoad;
import GeneExpressionDataOperation.GeneExpressionDataWrite;
import Filter.GeneFilterByTotalExpression;
import Filter.GeneFilterByVariance;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IGeneExpressionDataSource;
import Interfaces.IGeneExpressionDataWrite;

public class ClusterGenerationService {
    private static final String directoryPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\IR64";

    static void main() {
        int timeSeriesColumnIndex = 4;
        int replicateColumnIndex = 3;

        IGeneExpressionDataSource geneExpressionDataSource = new GeneExpressionDataLoad(directoryPath, replicateColumnIndex, timeSeriesColumnIndex);

        /// Hardcoded filters
        geneExpressionDataSource.addGeneFilter(new GeneFilterByTotalExpression(1000));
        geneExpressionDataSource.addGeneFilter(new GeneFilterByVariance(100));

        geneExpressionDataSource.addSampleFilter("Drought_Group", "Severe");
        geneExpressionDataSource.addSampleFilter("Condition", "Drought");

        GeneExpressionData geneExpressionData = geneExpressionDataSource.getGeneExpressionFormattedData();

        IClusteringAlgorithm kMeans = new KMeansAlgorithm(20, 100);
        GeneClusteringResult kMeansResult = kMeans.clusterGenes(geneExpressionData);

        IGeneExpressionDataWrite geneExpressionDataWrite = new GeneExpressionDataWrite();
        geneExpressionDataWrite.writeClusteringDataToFile(kMeansResult, directoryPath);
    }
}
