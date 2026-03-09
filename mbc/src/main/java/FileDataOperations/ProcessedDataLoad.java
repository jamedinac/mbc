package FileDataOperations;

import Common.GeneExpressionData;
import Enum.FileFormat;
import Utilities.FileUtilities;
import java.util.HashMap;

public class ProcessedDataLoad {
    public GeneExpressionData readProcessedData(String filePath) {
        FileFormat fileFormat = FileUtilities.detectFormat(filePath);
        String[] fileLines = FileUtilities.getFileLines(filePath);
        
        if (fileLines.length == 0) {
            throw new IllegalArgumentException("File is empty: " + filePath);
        }

        // Header: GeneID, Sample1, Sample2, ...
        String[] header = FileUtilities.getSplitDataRow(fileLines[0], fileFormat.getDelimiter());
        int numberOfSamples = header.length - 1;
        String[] sampleIds = new String[numberOfSamples];
        System.arraycopy(header, 1, sampleIds, 0, numberOfSamples);

        int numberOfGenes = fileLines.length - 1;
        String[] geneIds = new String[numberOfGenes];
        double[][] expressionData = new double[numberOfGenes][numberOfSamples];

        for (int i = 0; i < numberOfGenes; i++) {
            String[] row = FileUtilities.getSplitDataRow(fileLines[i + 1], fileFormat.getDelimiter());
            geneIds[i] = row[0];
            for (int j = 0; j < numberOfSamples; j++) {
                expressionData[i][j] = Double.parseDouble(row[j + 1]);
            }
        }

        // Return with empty metadata as normalization is already done. 
        // For processed data, we assume sampleIds represent the time points.
        return new GeneExpressionData(numberOfGenes, expressionData, geneIds, sampleIds, new HashMap<>(), new int[0], new int[0], sampleIds);
    }
}
