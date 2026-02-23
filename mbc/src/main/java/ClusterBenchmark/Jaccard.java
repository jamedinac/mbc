package ClusterBenchmark;

import Common.ClusterBenchmarkResult;
import Common.GeneClusterData;
import Common.GeneExpressionData;
import Enum.BenchmarkType;
import Interfaces.IClusterBenchmark;
import Utilities.ClusterDataUtilities;

import java.util.HashMap;

public class Jaccard implements IClusterBenchmark {

    GeneClusterData goldStandard;

    public Jaccard(GeneClusterData goldStandard) {
        this.goldStandard = goldStandard;
    }

    @Override
    public ClusterBenchmarkResult evaluate(GeneExpressionData geneExpressionData, GeneClusterData geneClusterData) {
        HashMap<String, Integer> predictedMap = ClusterDataUtilities.buildClusterMap(geneClusterData);
        HashMap<String, Integer> referenceMap = ClusterDataUtilities.buildClusterMap(goldStandard);
        String[] commonGenes = ClusterDataUtilities.getCommonGenes(predictedMap, referenceMap);

        int n = commonGenes.length;
        long a = 0; // same cluster in both predicted and reference
        long b = 0; // same in predicted only
        long c = 0; // same in reference only

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                boolean samePredicted = predictedMap.get(commonGenes[i]).equals(predictedMap.get(commonGenes[j]));
                boolean sameReference = referenceMap.get(commonGenes[i]).equals(referenceMap.get(commonGenes[j]));

                if (samePredicted && sameReference) {
                    a++;
                } else if (samePredicted) {
                    b++;
                } else if (sameReference) {
                    c++;
                }
            }
        }

        double jaccard = (a + b + c) == 0 ? 0.0 : (double) a / (a + b + c);

        return new ClusterBenchmarkResult(BenchmarkType.Jaccard, new double[geneClusterData.getNumberOfGenes()], jaccard, geneClusterData);
    }
}
