package org.example;

import Common.GeneClusterData;
import DataGenerators.RandomGenerator;
import Enum.FileFormat;
import FileDataOperations.GeneClusterDataWrite;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimulateDataGeneratorService {
    static String directoryPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated";

    static String dataFileName = "data";
    static String metadataFileName = "metadata";
    static String groundTruthFileName = "ground_truth";

    static int numberOfGenes = 100;
    static int numberOfReplicates = 3;
    static int numberOfTimeSeries = 13;

    static double replicateNoiseSd = 1.5;
    static double trajectoryNoiseSd = 0.3;
    static int maxDeltaVariation = 1;
    static int verticalOffsetRange = 10;

    public static void main() {
        int numberOfComponents = numberOfReplicates * numberOfTimeSeries;

        double[][] expressionData = new double[numberOfGenes][numberOfComponents];

        generateData(0, 4, expressionData,numberOfTimeSeries - 1);
        generateData(5, 9, expressionData, 0);
        generateData(10, 14, expressionData, numberOfTimeSeries / 2);
        generateData(15, 99, expressionData, -1);

        String[] geneIds = generateGeneIds();
        String[] columns = generateColumns();
        String[] metadataColumns = {"Sample", "Replicate", "Time"};
        String[][] metadata = generateMetadata(columns);

        writeExpressionData(directoryPath, geneIds, columns, expressionData);
        writeMetadata(directoryPath, metadataColumns, metadata);
        writeGroundTruth(directoryPath, geneIds);

    }

    private static void generateData(int startGene, int endGene, double[][] expressionData, int pivot) {
        int baseDelta = pivot == -1 ? 0 : 1;

        for (int gene = startGene; gene <= endGene; gene++) {
            int currentExpression = RandomGenerator.uniformRandomIntInRange(20, 50);

            int verticalOffset = RandomGenerator.uniformRandomIntInRange(-verticalOffsetRange, verticalOffsetRange);
            currentExpression += verticalOffset;
            currentExpression = Math.max(1, currentExpression);

            int delta = baseDelta == 0 ? 0 : RandomGenerator.uniformRandomIntInRange(baseDelta, baseDelta + maxDeltaVariation);

            for (int t = 0; t < numberOfTimeSeries; t++) {
                for (int r = 0; r < numberOfReplicates; r++) {
                    int replicateNoise = (int) Math.round(RandomGenerator.gaussianRandom(0, replicateNoiseSd));
                    expressionData[gene][t * numberOfReplicates + r] = Math.max(0, currentExpression + replicateNoise);
                }

                if (t == pivot) {
                    delta *= -1;
                }

                int trajectoryNoise = (int) Math.round(RandomGenerator.gaussianRandom(0, trajectoryNoiseSd));
                currentExpression += delta + trajectoryNoise;
                currentExpression = Math.max(0, currentExpression);
            }
        }
    }

    private static String[] generateGeneIds() {
        String[] geneIds = new String[numberOfGenes];

        for (int g = 0; g < numberOfGenes; g++) {
            geneIds[g] = "GENE" + (g+1);
        }

        return geneIds;
    }

    private static String[] generateColumns() {
        String[] columns = new String[numberOfTimeSeries*numberOfReplicates + 1];
        columns[0] = "GeneId";

        for (int c = 1; c <= numberOfTimeSeries*numberOfReplicates; c++) {
            columns[c] = "R" + ((c-1)%numberOfReplicates + 1) + "T" + ((c-1)/numberOfReplicates);
        }

        return columns;
    }

    private static String[][] generateMetadata(String[] columns) {
        String[][] metadata = new String[numberOfTimeSeries*numberOfReplicates][3];

        for (int t = 1; t <= numberOfTimeSeries*numberOfReplicates; t++) {
            metadata[t-1][0] = columns[t];
            metadata[t-1][1] = Integer.toString((t-1) % numberOfReplicates + 1);
            metadata[t-1][2] = Integer.toString((t-1) / numberOfReplicates);
        }

        return metadata;
    }

    private static void writeExpressionData(String directoryPath, String[] geneIds, String[] columns, double[][] expressionData) {
        String separator = FileFormat.TSV.getDelimiter();
        String extension = FileFormat.TSV.getExtension();

        try {
            String expressionDataFilePath = directoryPath + File.separator + dataFileName + extension;
            StringBuilder fileContent = new StringBuilder();

            for(String column : columns) {
                fileContent.append(column).append(separator);
            }
            fileContent.append("\n");

            for (int g=0; g < numberOfGenes; g++) {
                fileContent.append(geneIds[g]).append(separator);
                for (int c=0; c<expressionData[g].length; ++c) {
                    fileContent.append(Math.round(expressionData[g][c])).append(separator);
                }
                fileContent.append("\n");
            }

            Files.writeString(Paths.get(expressionDataFilePath), fileContent.toString());
        } catch(Exception e) {
            System.out.println("Error writing gene expression file: " + e.getMessage());
        }
    }

    private static void writeMetadata(String directoryPath, String[] metadataColumns, String[][] metadata) {
        String separator = FileFormat.TSV.getDelimiter();
        String extension = FileFormat.TSV.getExtension();
        try {
            String dataFilePath = directoryPath + File.separator + metadataFileName + extension;
            StringBuilder fileContent = new StringBuilder();

            for(String column : metadataColumns) {
                fileContent.append(column).append(separator);
            }
            fileContent.append("\n");

            for(String[] row : metadata) {
                for(String c : row) {
                    fileContent.append(c).append(separator);
                }
                fileContent.append("\n");
            }

            Files.writeString(Paths.get(dataFilePath), fileContent.toString());
        } catch(Exception e) {
            System.out.println("Error writing metadata file: " + e.getMessage());
        }
    }

    private static void writeGroundTruth(String directoryPath, String[] geneIds) {
        int numberOfClusters = 4;
        double[][] clusteringData = new double[numberOfGenes][numberOfClusters];

        for (int g = 0; g < numberOfGenes; g++) {
            int cluster;
            if (g <= 4) {
                cluster = 0;
            } else if (g <= 9) {
                cluster = 1;
            } else if (g <= 14) {
                cluster = 2;
            } else {
                cluster = 3;
            }
            clusteringData[g][cluster] = 1.0;
        }

        GeneClusterData groundTruth = new GeneClusterData(numberOfGenes, numberOfClusters, geneIds, clusteringData);
        String outputPath = directoryPath + File.separator + groundTruthFileName;
        new GeneClusterDataWrite().writeClusteringDataToFile(groundTruth, outputPath);
    }
}
