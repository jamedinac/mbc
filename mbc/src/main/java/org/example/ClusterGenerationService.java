package org.example;

import ClusteringAlgorithms.KMeansAlgorithm;
import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import GeneExpressionDataOperation.GeneExpressionDataLoad;
import GeneExpressionDataOperation.GeneExpressionDataWrite;
import Interfaces.IClusteringAlgorithm;
import Interfaces.IGeneExpressionDataSource;
import Interfaces.IGeneExpressionDataWrite;

public class ClusterGenerationService {
    private static final String directoryPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\IR64";

    static void main() {
        IGeneExpressionDataSource geneExpressionDataSource = new GeneExpressionDataLoad(directoryPath);

        int numberOfReplicates = 3;
        int numberOfTimeSeries = 12;

        GeneExpressionData geneExpressionData = geneExpressionDataSource.getGeneExpressionFormattedData(numberOfTimeSeries, numberOfReplicates);

        IClusteringAlgorithm kMeans = new KMeansAlgorithm(5, 10);
        GeneClusteringResult kMeansResult = kMeans.clusterGenes(geneExpressionData);

        IGeneExpressionDataWrite geneExpressionDataWrite = new GeneExpressionDataWrite();
        geneExpressionDataWrite.writeClusteringDataToFile(kMeansResult, directoryPath);
    }
}
