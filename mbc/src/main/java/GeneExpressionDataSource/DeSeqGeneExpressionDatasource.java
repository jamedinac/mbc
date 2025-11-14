package GeneExpressionDataSource;

import Common.GeneExpressionData;
import Interfaces.IGeneExpressionDataSource;

public class DeSeqGeneExpressionDatasource implements IGeneExpressionDataSource {

    public DeSeqGeneExpressionDatasource (String geneCountFile, String metadaFile) {

    }

    public GeneExpressionData getGeneExpressionFormattedData () {
        int[][] geneExpressionData = new int[1][1];
        return new GeneExpressionData(1, 1, 1, geneExpressionData);
    }
}
