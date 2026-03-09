package FileDataOperations;

import Common.GeneExpressionData;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GeneExpressionDataWrite {
    public void writeGeneExpressionDataToFile(GeneExpressionData data, String filePath) {
        try {
            StringBuilder sb = new StringBuilder();
            // Header: GeneID, Sample1, Sample2, ...
            sb.append("GeneID");
            String[] sampleIds = data.getSampleIds();
            for (String sampleId : sampleIds) {
                sb.append(",").append(sampleId);
            }
            sb.append("\n");

            // Rows: GeneName, Val1, Val2, ...
            double[][] expressionData = data.getExpressionData();
            for (int i = 0; i < data.getNumberOfGenes(); i++) {
                sb.append(data.getGeneId(i));
                for (int j = 0; j < expressionData[i].length; j++) {
                    sb.append(",").append(expressionData[i][j]);
                }
                sb.append("\n");
            }

            Files.writeString(Paths.get(filePath), sb.toString());
            System.out.println("DEBUG: Processed data successfully saved to: " + filePath);
        } catch (IOException e) {
            System.err.println("DEBUG ERROR: Error writing processed data: " + e.getMessage());
        }
    }
}
