package ClusterBenchmark;

import Common.*;
import Interfaces.IClusterBenchmark;
import Interfaces.IGeneDistance;

public class Silhouette implements IClusterBenchmark {
    IGeneDistance geneDistance;

    public Silhouette(IGeneDistance  geneDistance) {
        this.geneDistance = geneDistance;
    }

    @Override
    public ClusterBenchmarkResult evaluate(GeneExpressionData geneExpressionData, GeneClusterData geneClusterData) {
        int[] clusterId = new int[geneClusterData.getNumberOfGenes()];
        int[] clusterSize = new int[geneClusterData.getNumberOfClusters()];

        for (int g = 0; g < geneClusterData.getNumberOfGenes(); g++) {
            clusterId[g] = this.getGeneClusterId(g, geneClusterData);
            clusterSize[clusterId[g]]++;
        }

        double[] similarity = new double[geneClusterData.getNumberOfGenes()];
        double[] dissimilarity = new double[geneClusterData.getNumberOfGenes()];
        double[] silhoutte = new  double[geneClusterData.getNumberOfGenes()];

        for (int currentGene = 0; currentGene< geneClusterData.getNumberOfGenes(); currentGene++) {
            double[] distance = new double[geneClusterData.getNumberOfClusters()];

            for (int iteratingGene = 0; iteratingGene < geneClusterData.getNumberOfGenes(); iteratingGene++) {
                if (iteratingGene != currentGene) {
                    distance[clusterId[iteratingGene]] += this.geneDistance.getDistance(geneExpressionData.getGeneProfile(currentGene), geneExpressionData.getGeneProfile(iteratingGene));
                }
            }

            for (int cluster = 0; cluster < geneClusterData.getNumberOfClusters(); cluster++) {
                if (cluster != clusterId[currentGene]) {
                    distance[cluster] /= clusterSize[cluster];
                    dissimilarity[currentGene] = distance[cluster];
                } else {
                    distance[cluster] = clusterSize[cluster] == 1 ? 0 : distance[cluster] / (clusterSize[cluster] - 1);
                }
            }

            similarity[currentGene] = distance[clusterId[currentGene]];
            for (int cluster = 0; cluster < geneClusterData.getNumberOfClusters(); cluster++) {
                if (cluster != clusterId[currentGene]) {
                    dissimilarity[currentGene] = Math.min(dissimilarity[currentGene], distance[cluster]);
                }
            }
        }

        double meanSilhouette = 0;
        for (int gene = 0; gene < geneClusterData.getNumberOfGenes(); gene++) {
            if (clusterSize[clusterId[gene]] == 1) {
                silhoutte[gene] = 0;
            } else {
                silhoutte[gene] = (dissimilarity[gene] - similarity[gene]) / Math.max(similarity[gene], dissimilarity[gene]);
            }
            meanSilhouette += silhoutte[gene];
        }

        return new ClusterBenchmarkResult(BenchmarkType.Silhouette, silhoutte ,meanSilhouette / geneClusterData.getNumberOfGenes(), geneClusterData);
    }

    private int getGeneClusterId(int g, GeneClusterData geneClusterData) {
        int clusterId = -1;
        for(int c = 0; c < geneClusterData.getNumberOfClusters(); c++) {
            if (geneClusterData.getClusteringData()[g][c] == 1.0) {
                clusterId = c;
            }
        }
        return clusterId;
    }
}
