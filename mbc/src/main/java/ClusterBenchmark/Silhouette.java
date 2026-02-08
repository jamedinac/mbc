package ClusterBenchmark;

import Common.BenchmarkType;
import Common.ClusterBenchmarkResult;
import Common.GeneClusteringResult;
import Interfaces.IClusterBenchmark;
import Utilities.GeneOperations;

public class Silhouette implements IClusterBenchmark {

    GeneClusteringResult geneClustering;

    public Silhouette(GeneClusteringResult geneClustering) {
        this.geneClustering = geneClustering;
    }

    @Override
    public ClusterBenchmarkResult evaluate() {
        int[] clusterId = new int[geneClustering.getNumberOfGenes()];
        int[] clusterSize = new int[geneClustering.getNumberOfClusters()];

        for (int g=0; g < geneClustering.getNumberOfGenes(); g++) {
            clusterId[g] = this.getGeneClusterId(g, this.geneClustering);
            clusterSize[clusterId[g]]++;
        }

        double[] similarity = new double[geneClustering.getNumberOfGenes()];
        double[] dissimilarity = new double[geneClustering.getNumberOfGenes()];
        double[] silhoutte = new  double[geneClustering.getNumberOfGenes()];

        for (int currentGene=0; currentGene<geneClustering.getNumberOfGenes(); currentGene++) {
            double[] distance = new double[geneClustering.getNumberOfClusters()];

            for (int iteratingGene=0; iteratingGene < geneClustering.getNumberOfGenes(); iteratingGene++) {
                if (iteratingGene != currentGene) {
                    distance[clusterId[iteratingGene]] += GeneOperations.euclideanDistance(geneClustering.getGeneExpressionData().getGeneProfile(currentGene), geneClustering.getGeneExpressionData().getGeneProfile(iteratingGene));
                }
            }

            for (int cluster = 0;  cluster < geneClustering.getNumberOfClusters(); cluster++) {
                if (cluster != clusterId[currentGene]) {
                    distance[cluster] /= clusterSize[cluster];
                    dissimilarity[currentGene] = distance[cluster];
                } else {
                    distance[cluster] = clusterSize[cluster] == 1 ? 0 : distance[cluster] / (clusterSize[cluster] - 1);
                }
            }

            similarity[currentGene] = distance[clusterId[currentGene]];
            for (int cluster = 0;  cluster < geneClustering.getNumberOfClusters(); cluster++) {
                if (cluster != clusterId[currentGene]) {
                    dissimilarity[currentGene] = Math.min(dissimilarity[currentGene], distance[cluster]);
                }
            }
        }

        double meanSilhouette = 0;
        for (int gene = 0; gene < geneClustering.getNumberOfGenes(); gene++) {
            if (clusterSize[clusterId[gene]] == 1) {
                silhoutte[gene] = 0;
            } else {
                silhoutte[gene] = (dissimilarity[gene] - similarity[gene]) / Math.max(similarity[gene], dissimilarity[gene]);
            }
            meanSilhouette += silhoutte[gene];
        }

        return new ClusterBenchmarkResult(BenchmarkType.Silhouette, silhoutte ,meanSilhouette / this.geneClustering.getNumberOfGenes(), this.geneClustering);
    }

    private int getGeneClusterId(int g, GeneClusteringResult geneClustering) {
        int clusterId = -1;
        for(int c=0; c<geneClustering.getNumberOfClusters(); c++) {
            if (geneClustering.getGeneClusteringData()[g][c] == 1.0) {
                clusterId = c;
            }
        }
        return clusterId;
    }
}
