package org.example;

import DataGenerators.RandomGenerator;
import Enum.FileFormat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimulateDataGeneratorService {

    static String dataFileName = "data";
    static String metadataFileName = "metadata";

    static int numberOfGenes = 100;
    static int numberOfReplicates = 3;
    static int numberOfTimeSeries = 13;

    public static void main() {

        String directoryPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated\\data.csv";
        double[][] expressionData = new double[numberOfGenes][numberOfReplicates * numberOfTimeSeries];
        generateData(0, 4, expressionData);
        generateData(5, 9, expressionData);
        generateData(10, 14, expressionData);
        generateData(15, 99, expressionData);

        String[] geneIds = generateGeneIds();
        String[] columns = generateColumns();
        String[] metadataColumns = {"Sample", "Replicate", "Time"};
        String[][] metadata = generateMetadata(columns);

        writeExpressionData(directoryPath, geneIds, columns, expressionData);
        writeMetadata(directoryPath, metadataColumns, metadata);

    }

    private static void generateData(int startGene, int endGene, double[][] expressionData) {
        int[] expressionAddition = new int[numberOfTimeSeries];
        for (int t=0; t<numberOfTimeSeries; t++) {
            expressionAddition[t] = RandomGenerator.uniformRandomInt(100);
            if (RandomGenerator.randomBoolean()) {
                expressionAddition[t] *= -1;
            }
        }

        for (int gene = startGene; gene <= endGene; gene++) {
            int currentExpression = RandomGenerator.uniformRandomIntInRange(1, 50);

            for (int t = 0; t < numberOfTimeSeries; t++) {
                for (int r = 0; r < numberOfReplicates; r++) {
                    expressionData[gene][t*numberOfReplicates + r] = Math.round(RandomGenerator.gaussianRandom(currentExpression, 1.0));
                    expressionData[gene][t*numberOfReplicates + r] = Math.max(1, expressionData[gene][t*numberOfReplicates + r]);
                }

                currentExpression += expressionAddition[t] + RandomGenerator.uniformRandomIntInRange(1, 3);
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
        String separator = FileFormat.CSV.getDelimiter();
        String extension = FileFormat.CSV.getExtension();
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
}
