package GeneExpressionDataSource;

import Interfaces.IGeneExpressionDataSource;

public class DeSeqGeneExpressionDatasource implements IGeneExpressionDataSource {

    public DeSeqGeneExpressionDatasource (String geneCountFile, String metadaFile) {

    }

    public int [][] getGeneExpressionFormattedData () {
        return new int [1][1];
    }
}
