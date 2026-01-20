package ClusteringAlgorithms;

import Common.GeneClusteringResult;
import Common.GeneExpressionData;
import Interfaces.IClusteringAlgorithm;

import java.util.ArrayList;

/**
 * Abu-Jamous and Kelly Genome Biology
 * https://doi.org/10.1186/s13059-018-1536-8
 */
public class ClustAlgorithm implements IClusteringAlgorithm {

    static class ClustParameters {
        ArrayList<IClusteringAlgorithm> clusteringAlgorithms;
        ArrayList<Integer> kValues;
        ArrayList<Double> deltaValues;
        Double tightness;
        Integer smallestCluster;
        Double thirdQuartiles;
    };

    static class MNScatterPlotParameters {
        GeneClusteringResult geneClusteringElite;
        ArrayList<Double> mnDistances;
    }

    private final ClustParameters parameters;

    public ClustAlgorithm(ClustParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public GeneClusteringResult clusterGenes(GeneExpressionData geneExpresionData) {
        GeneClusteringResult seed = bicopam(geneExpresionData);
        MNScatterPlotParameters mnScatterPlotParameters = mnScatterPlot(geneExpresionData, seed);
        GeneClusteringResult finalClusters = optimise(geneExpresionData, mnScatterPlotParameters);
        return finalClusters;
    }

    private GeneClusteringResult bicopam(GeneExpressionData geneExpressionData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private MNScatterPlotParameters mnScatterPlot (GeneExpressionData geneExpressionData, GeneClusteringResult seed) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private GeneClusteringResult optimise(GeneExpressionData geneExpressionData, MNScatterPlotParameters mnScatterPlotParameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}