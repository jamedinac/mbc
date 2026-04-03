package FileDataOperations;

import Enum.FileFormat;
import Common.GeneClusterData;
import Utilities.FileUtilities;

public class GeneClusterDataLoad {

    String filename;

    public GeneClusterDataLoad(String fileName ) {
        this.filename = fileName;
    }

    public GeneClusterData readClusterData() {
        String[] fileLines = FileUtilities.getFileLines(filename);

        int numberOfGenes = fileLines.length;
        int numberOfClusters = FileUtilities.getSplitDataRow(fileLines[0], FileFormat.TSV.getDelimiter()).length - 1;

        String[] geneIds = new String[numberOfGenes];
        double[][] clusterData = new double[numberOfGenes][numberOfClusters];

        for (int g=0; g<numberOfGenes;g++){
            String[] dataRow = FileUtilities.getSplitDataRow(fileLines[g], FileFormat.TSV.getDelimiter());
            geneIds[g] = dataRow[0];
            double sum = 0.0;

            for (int c = 1; c < dataRow.length; c++){
                double value = Double.parseDouble(dataRow[c]);
                clusterData[g][c-1] = value;
                sum += value;
            }

            if (Math.abs(sum - 1.0) > 1e-6) {
                throw new IllegalArgumentException("Gene " + geneIds[g] + " has an invalid cluster assignment. The sum of cluster values must be 1.0, but was " + sum);
            }
        }

        return new GeneClusterData(numberOfGenes, numberOfClusters, geneIds, clusterData);
    }
}
