package FileDataOperations;

import Enum.FileFormat;
import Common.GeneClusterData;
import Utilities.FileUtilities;

import java.io.File;

public class GeneClusterDataLoad {

    String directoryPath;
    String filename;

    public GeneClusterDataLoad(String directoryPath, String fileName ) {
        this.directoryPath = directoryPath;
        this.filename = fileName;
    }

    public GeneClusterData readClusterData() {
        String[] fileLines = FileUtilities.getFileLines(directoryPath + File.separator + filename);

        int numberOfGenes = fileLines.length;
        int numberOfClusters = FileUtilities.getSplitDataRow(fileLines[0], FileFormat.TSV.getDelimiter()).length - 1;

        String[] geneIds = new String[numberOfGenes];
        double[][] clusterData = new double[numberOfGenes][numberOfClusters];

        for (int g=0; g<numberOfGenes;g++){
            String[] dataRow = FileUtilities.getSplitDataRow(fileLines[g], FileFormat.TSV.getDelimiter());
            geneIds[g] = dataRow[0];

            for (int c = 1; c < dataRow.length; c++){
                clusterData[g][c-1] = Double.parseDouble(dataRow[c]);
            }
        }

        return new GeneClusterData(numberOfGenes, numberOfClusters, geneIds, clusterData);
    }
}
