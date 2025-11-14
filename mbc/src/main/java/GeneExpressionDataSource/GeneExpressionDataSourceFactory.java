package GeneExpressionDataSource;

import Common.GeneExpressionType;
import Interfaces.IGeneExpressionDataSource;

public class GeneExpressionDataSourceFactory {
    public static IGeneExpressionDataSource createDataSource(GeneExpressionType geneExpressionType) {
        switch (geneExpressionType) {
           case DeSeq:
               return new DeSeqGeneExpressionDatasource("", "");
            case Simulated:
                return new SimulatedGeneExpressionDatasource();
            default:
                throw  new IllegalArgumentException("Invalid gene expression type");
        }
    }
}
