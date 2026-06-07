package Significance;

import Interfaces.IMultipleTestingCorrection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BenjaminiHochbergCorrection implements IMultipleTestingCorrection {

    private record PValueEntry(int geneIndex, int timePointIndex, double pValue) implements Comparable<PValueEntry> {
        @Override
        public int compareTo(PValueEntry o) {
            return Double.compare(this.pValue, o.pValue);
        }
    }

    @Override
    public double[][] adjustPValues(double[][] rawPValues) {
        int nGenes = rawPValues.length;
        if (nGenes == 0) return new double[0][0];
        int nTimePoints = rawPValues[0].length;

        // Total tests equals nGenes * (nTimePoints - 1) because we skip the intercept (j=0)
        int totalTests = nGenes * (nTimePoints - 1);
        List<PValueEntry> entries = new ArrayList<>(totalTests);

        for (int i = 0; i < nGenes; i++) {
            // Start j at 1 to ignore the intercept p-value
            for (int j = 1; j < nTimePoints; j++) {
                entries.add(new PValueEntry(i, j, rawPValues[i][j]));
            }
        }

        Collections.sort(entries);

        double[] adjusted = new double[totalTests];
        for (int k = 1; k <= totalTests; k++) {
            PValueEntry entry = entries.get(k - 1);
            double rawP = entry.pValue();
            double adjP = rawP * ((double) totalTests / k);
            adjusted[k - 1] = Math.min(adjP, 1.0);
        }

        for (int k = totalTests - 2; k >= 0; k--) {
            adjusted[k] = Math.min(adjusted[k], adjusted[k + 1]);
        }

        double[][] adjPValuesMatrix = new double[nGenes][nTimePoints];
        // Ensure intercept p-values remain 1.0 since they were not tested
        for (int i = 0; i < nGenes; i++) {
            adjPValuesMatrix[i][0] = 1.0;
        }
        
        for (int k = 0; k < totalTests; k++) {
            PValueEntry entry = entries.get(k);
            adjPValuesMatrix[entry.geneIndex()][entry.timePointIndex()] = adjusted[k];
        }

        return adjPValuesMatrix;
    }
}