package org.example;

import Common.GeneClusterData;
import DataGenerators.RandomGenerator;
import Enum.FileFormat;
import FileDataOperations.GeneClusterDataWrite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

/**
 * Simulates time-series RNA-seq expression data for 1000 genes across 4 clusters:
 * - Clusters 0, 2: Upward trending trajectories
 * - Cluster 1: Downward trending trajectories
 * - Cluster 3: Basal (flat) expression with noise
 *
 * Output: TSV files for expression data, metadata, and ground-truth cluster assignments.
 */
public class SimulateDataGeneratorService {

    // ========== CONFIGURATION (keep hardcoded for local testing) ==========
    static String directoryPath = "C:\\Users\\jhers\\OneDrive - Universidad de los Andes\\Materias\\Proyecto\\data\\Simulated";

    static String dataFileName = "data";
    static String metadataFileName = "metadata";
    static String groundTruthFileName = "ground_truth";

    // Experimental design
    static int numberOfGenes = 1000;
    static int numberOfReplicates = 3;
    static int numberOfTimeSeries = 13;

    // Trajectory parameters
    static double trajectoryNoiseSd = 0.3;
    static int maxDeltaVariation = 2;  // Increased from 1 for stronger signal
    static int minExpressionValue = 1;

    // Per-gene multiplicative scaling within a cluster (log-uniform)
    static double scaleFactorMin = 0.3;
    static double scaleFactorMax = 3.0;

    // Basal cluster expression levels
    static int basalLevelMin = 5;
    static int basalLevelMax = 80;

    // Sample-level technical noise (batch effect)
    static double sampleNoiseSdBase = 0.10;  // Reduced from 0.15
    static double sampleNoiseSdHigh = 0.40;
    static double badSampleProbability = 0.15;

    // Gene-specific biological noise
    static double geneNoiseFactorMin = 0.05;
    static double geneNoiseFactorMax = 0.15;  // Reduced from 0.25

    // Technical outliers
    static double outlierProbability = 0.01;
    static double outlierMultiplierMin = 2.0;
    static double outlierMultiplierMax = 4.0;

    // Random seed for reproducibility
    static long simulationSeed = 42;
    static Random rng = new Random(simulationSeed);

    // Drift control: reduced probability to preserve trend direction
    static double driftProbability = 0.10;  // Reduced from 0.30

    // ========== ENTRY POINT ==========

    /**
     * Main entry point for simulation.
     * @param args optional: [0] = output directory path (overrides hardcoded default)
     */
    public static void main(String[] args) {
        // Allow command-line override of output path while keeping default for convenience
        if (args.length > 0 && !args[0].isEmpty()) {
            directoryPath = args[0];
        }

        // Ensure output directory exists
        try {
            Files.createDirectories(Paths.get(directoryPath));
        } catch (IOException e) {
            System.err.println("Failed to create output directory: " + directoryPath);
            e.printStackTrace();
            return;
        }

        System.out.println("Starting simulation with seed=" + simulationSeed + ", output=" + directoryPath);

        int numberOfComponents = numberOfReplicates * numberOfTimeSeries;
        double[][] expressionData = new double[numberOfGenes][numberOfComponents];

        double[] sampleNoiseFactors = generateSampleNoiseFactors(numberOfComponents);
        double[] geneNoiseFactors = generateGeneNoiseFactors();

        // Generate trajectories for each cluster
        generateEarlyResponseData(0, 99, expressionData, sampleNoiseFactors, geneNoiseFactors);    // Cluster 0: upward (steep, early-response)
        generateDescendingData(100, 199, expressionData, sampleNoiseFactors, geneNoiseFactors);   // Cluster 1: downward (linear)
        generateSigmoidalData(200, 299, expressionData, sampleNoiseFactors, geneNoiseFactors);     // Cluster 2: upward (delayed, sigmoidal-response)
        generateBasalData(300, 999, expressionData, sampleNoiseFactors, geneNoiseFactors);         // Cluster 3: basal

        // Generate metadata
        String[] geneIds = generateGeneIds();
        String[] columns = generateColumns();
        String[] metadataColumns = {"Sample", "Time"};
        String[][] metadata = generateMetadata(columns);

        // Write outputs
        writeExpressionData(directoryPath, geneIds, columns, expressionData);
        writeMetadata(directoryPath, metadataColumns, metadata);
        writeGroundTruth(directoryPath, geneIds);

        // Run diagnostics
        System.out.println("\n=== Simulation Diagnostics ===");
        printClusterTrajectories(expressionData);
        printWithinClusterCorrelations(expressionData);
        validateSimulation(expressionData);

        System.out.println("\n✓ Simulation complete. Files written to: " + directoryPath);
    }

    // ========== NOISE GENERATION ==========

    /**
     * Generates per-sample technical noise factors (coefficient of variation).
     * Some samples are marked as "bad" with higher noise.
     */
    private static double[] generateSampleNoiseFactors(int numberOfSamples) {
        double[] factors = new double[numberOfSamples];
        for (int s = 0; s < numberOfSamples; s++) {
            boolean isBadSample = rng.nextDouble() < badSampleProbability;
            factors[s] = isBadSample ? sampleNoiseSdHigh : sampleNoiseSdBase;
        }
        return factors;
    }

    /**
     * Generates per-gene biological noise factors (coefficient of variation).
     */
    private static double[] generateGeneNoiseFactors() {
        double[] factors = new double[numberOfGenes];
        for (int g = 0; g < numberOfGenes; g++) {
            factors[g] = geneNoiseFactorMin +
                    rng.nextDouble() * (geneNoiseFactorMax - geneNoiseFactorMin);
        }
        return factors;
    }

    /**
     * Applies realistic noise to a true expression value.
     * Uses log-normal noise (multiplicative) to better reflect RNA-seq count properties.
     * Outliers compound with noise rather than replacing it.
     */
    private static double applyRealisticNoise(double trueExpression, int gene, int sampleIndex,
                                              double[] sampleNoiseFactors, double[] geneNoiseFactors) {
        // Combine noise sources in quadrature (CV adds in quadrature for independent log-normal errors)
        double combinedCV = Math.sqrt(
                Math.pow(sampleNoiseFactors[sampleIndex], 2) +
                        Math.pow(geneNoiseFactors[gene], 2)
        );

        // Log-normal noise: multiplicative on original scale, additive on log scale
        double logNoise = rng.nextGaussian() * combinedCV;
        double observed = trueExpression * Math.exp(logNoise);

        // Apply technical outlier (compounds with existing noise)
        if (rng.nextDouble() < outlierProbability) {
            double multiplier = outlierMultiplierMin +
                    rng.nextDouble() * (outlierMultiplierMax - outlierMultiplierMin);
            if (rng.nextDouble() < 0.5) {
                observed *= multiplier;      // Upward outlier
            } else {
                observed /= multiplier;      // Downward outlier
            }
        }

        // Enforce minimum expression value
        return Math.max(minExpressionValue, observed);
    }

    /**
     * Samples a scale factor from a log-uniform distribution.
     * This ensures multiplicative effects are symmetric on the log scale.
     */
    private static double sampleLogUniformScaleFactor() {
        double logMin = Math.log(scaleFactorMin);
        double logMax = Math.log(scaleFactorMax);
        return Math.exp(logMin + rng.nextDouble() * (logMax - logMin));
    }

    // ========== TRAJECTORY GENERATION ==========

    /**
     * Generates expression trajectories for genes with monotonic trends.
     *
     * @param startGene inclusive start index
     * @param endGene inclusive end index
     * @param expressionData output array [gene][sample]
     * @param baseTrend direction of trajectory: +1 (up), -1 (down)
     * @param sampleNoiseFactors per-sample technical noise SDs
     * @param geneNoiseFactors per-gene biological noise CVs
     */
    /**
     * Generates expression trajectories for Cluster 0 (steep, early-response upward trend).
     */
    private static void generateEarlyResponseData(int startGene, int endGene, double[][] expressionData,
                                                  double[] sampleNoiseFactors, double[] geneNoiseFactors) {
        double[] baseTrajectory = new double[numberOfTimeSeries];
        double vStart = 20.0;
        double vEnd = 80.0;
        double k = 0.45;

        for (int t = 0; t < numberOfTimeSeries; t++) {
            double val = vStart + (vEnd - vStart) * (1.0 - Math.exp(-k * t));
            double trajectoryNoise = rng.nextGaussian() * trajectoryNoiseSd;
            baseTrajectory[t] = Math.max(minExpressionValue, val + trajectoryNoise);
        }

        for (int gene = startGene; gene <= endGene; gene++) {
            double scaleFactor = sampleLogUniformScaleFactor();
            for (int t = 0; t < numberOfTimeSeries; t++) {
                double trueExpression = Math.max(minExpressionValue, baseTrajectory[t] * scaleFactor);
                for (int r = 0; r < numberOfReplicates; r++) {
                    int sampleIndex = t * numberOfReplicates + r;
                    expressionData[gene][sampleIndex] = applyRealisticNoise(
                            trueExpression, gene, sampleIndex, sampleNoiseFactors, geneNoiseFactors);
                }
            }
        }
    }

    /**
     * Generates expression trajectories for Cluster 2 (delayed sigmoidal-response upward trend).
     */
    private static void generateSigmoidalData(int startGene, int endGene, double[][] expressionData,
                                              double[] sampleNoiseFactors, double[] geneNoiseFactors) {
        double[] baseTrajectory = new double[numberOfTimeSeries];
        double vStart = 20.0;
        double vEnd = 80.0;
        double t0 = 6.0;
        double k = 0.8;

        for (int t = 0; t < numberOfTimeSeries; t++) {
            double val = vStart + (vEnd - vStart) / (1.0 + Math.exp(-k * (t - t0)));
            double trajectoryNoise = rng.nextGaussian() * trajectoryNoiseSd;
            baseTrajectory[t] = Math.max(minExpressionValue, val + trajectoryNoise);
        }

        for (int gene = startGene; gene <= endGene; gene++) {
            double scaleFactor = sampleLogUniformScaleFactor();
            for (int t = 0; t < numberOfTimeSeries; t++) {
                double trueExpression = Math.max(minExpressionValue, baseTrajectory[t] * scaleFactor);
                for (int r = 0; r < numberOfReplicates; r++) {
                    int sampleIndex = t * numberOfReplicates + r;
                    expressionData[gene][sampleIndex] = applyRealisticNoise(
                            trueExpression, gene, sampleIndex, sampleNoiseFactors, geneNoiseFactors);
                }
            }
        }
    }

    /**
     * Generates expression trajectories for Cluster 1 (clear linear downward trend).
     */
    private static void generateDescendingData(int startGene, int endGene, double[][] expressionData,
                                               double[] sampleNoiseFactors, double[] geneNoiseFactors) {
        double[] baseTrajectory = new double[numberOfTimeSeries];
        double vStart = 20.0;
        double vEnd = 80.0;

        for (int t = 0; t < numberOfTimeSeries; t++) {
            double val = vEnd - (vEnd - vStart) * t / (numberOfTimeSeries - 1);
            double trajectoryNoise = rng.nextGaussian() * trajectoryNoiseSd;
            baseTrajectory[t] = Math.max(minExpressionValue, val + trajectoryNoise);
        }

        for (int gene = startGene; gene <= endGene; gene++) {
            double scaleFactor = sampleLogUniformScaleFactor();
            for (int t = 0; t < numberOfTimeSeries; t++) {
                double trueExpression = Math.max(minExpressionValue, baseTrajectory[t] * scaleFactor);
                for (int r = 0; r < numberOfReplicates; r++) {
                    int sampleIndex = t * numberOfReplicates + r;
                    expressionData[gene][sampleIndex] = applyRealisticNoise(
                            trueExpression, gene, sampleIndex, sampleNoiseFactors, geneNoiseFactors);
                }
            }
        }
    }

    /**
     * Generates flat (basal) expression for non-differentially expressed genes.
     */
    private static void generateBasalData(int startGene, int endGene, double[][] expressionData,
                                          double[] sampleNoiseFactors, double[] geneNoiseFactors) {
        for (int gene = startGene; gene <= endGene; gene++) {
            double basalLevel = basalLevelMin + rng.nextInt(basalLevelMax - basalLevelMin + 1);

            for (int t = 0; t < numberOfTimeSeries; t++) {
                for (int r = 0; r < numberOfReplicates; r++) {
                    int sampleIndex = t * numberOfReplicates + r;
                    expressionData[gene][sampleIndex] = applyRealisticNoise(
                            basalLevel, gene, sampleIndex, sampleNoiseFactors, geneNoiseFactors);
                }
            }
        }
    }

    // ========== METADATA & IDENTIFIERS ==========

    private static String[] generateGeneIds() {
        String[] geneIds = new String[numberOfGenes];
        for (int g = 0; g < numberOfGenes; g++) {
            geneIds[g] = "GENE" + (g + 1);
        }
        return geneIds;
    }

    private static String[] generateColumns() {
        String[] columns = new String[numberOfTimeSeries * numberOfReplicates + 1];
        columns[0] = "GeneId";

        for (int c = 1; c <= numberOfTimeSeries * numberOfReplicates; c++) {
            columns[c] = "R" + ((c - 1) % numberOfReplicates + 1) + "T" + ((c - 1) / numberOfReplicates);
        }
        return columns;
    }

    private static String[][] generateMetadata(String[] columns) {
        String[][] metadata = new String[numberOfTimeSeries * numberOfReplicates][2];

        for (int t = 1; t <= numberOfTimeSeries * numberOfReplicates; t++) {
            metadata[t - 1][0] = columns[t];
            metadata[t - 1][1] = Integer.toString((t - 1) / numberOfReplicates);
        }
        return metadata;
    }

    // ========== FILE I/O ==========

    private static void writeExpressionData(String directoryPath, String[] geneIds, String[] columns, double[][] expressionData) {
        String separator = FileFormat.TSV.getDelimiter();
        String extension = FileFormat.TSV.getExtension();

        try {
            String expressionDataFilePath = directoryPath + File.separator + dataFileName + extension;
            StringBuilder fileContent = new StringBuilder();

            // Header
            for (String column : columns) {
                fileContent.append(column).append(separator);
            }
            fileContent.append("\n");

            // Data rows
            for (int g = 0; g < numberOfGenes; g++) {
                fileContent.append(geneIds[g]).append(separator);
                for (double value : expressionData[g]) {
                    fileContent.append(Math.round(value)).append(separator);
                }
                fileContent.append("\n");
            }

            Files.writeString(Paths.get(expressionDataFilePath), fileContent.toString());
            System.out.println("✓ Wrote expression data: " + expressionDataFilePath);

        } catch (IOException e) {
            System.err.println("Failed to write expression data: " + e.getMessage());
            throw new RuntimeException("Simulation failed during file output", e);
        }
    }

    private static void writeMetadata(String directoryPath, String[] metadataColumns, String[][] metadata) {
        String separator = FileFormat.TSV.getDelimiter();
        String extension = FileFormat.TSV.getExtension();

        try {
            String dataFilePath = directoryPath + File.separator + metadataFileName + extension;
            StringBuilder fileContent = new StringBuilder();

            // Header
            for (String column : metadataColumns) {
                fileContent.append(column).append(separator);
            }
            fileContent.append("\n");

            // Data rows
            for (String[] row : metadata) {
                for (String value : row) {
                    fileContent.append(value).append(separator);
                }
                fileContent.append("\n");
            }

            Files.writeString(Paths.get(dataFilePath), fileContent.toString());
            System.out.println("✓ Wrote metadata: " + dataFilePath);

        } catch (IOException e) {
            System.err.println("Failed to write metadata: " + e.getMessage());
            throw new RuntimeException("Simulation failed during file output", e);
        }
    }

    private static void writeGroundTruth(String directoryPath, String[] geneIds) {
        int numberOfClusters = 4;
        double[][] clusteringData = new double[numberOfGenes][numberOfClusters];

        for (int g = 0; g < numberOfGenes; g++) {
            int cluster;
            if (g <= 99) {
                cluster = 0;
            } else if (g <= 199) {
                cluster = 1;
            } else if (g <= 299) {
                cluster = 2;
            } else {
                cluster = 3;
            }
            clusteringData[g][cluster] = 1.0;
        }

        GeneClusterData groundTruth = new GeneClusterData(numberOfGenes, numberOfClusters, geneIds, clusteringData);
        String outputPath = directoryPath + File.separator + groundTruthFileName;

        try {
            new GeneClusterDataWrite().writeClusteringDataToFile(groundTruth, outputPath);
            System.out.println("✓ Wrote ground truth: " + outputPath);
        } catch (Exception e) {
            System.err.println("Failed to write ground truth: " + e.getMessage());
            throw new RuntimeException("Simulation failed during ground truth output", e);
        }
    }

    // ========== DIAGNOSTICS & VALIDATION ==========

    /**
     * Prints mean expression trajectory per cluster (averaged across genes and replicates).
     * Use this to verify that clusters 0/2 increase, cluster 1 decreases, cluster 3 is flat.
     */
    private static void printClusterTrajectories(double[][] expressionData) {
        int[][] clusterRanges = {{0, 99}, {100, 199}, {200, 299}, {300, 999}};
        String[] clusterLabels = {"Cluster 0 (up)", "Cluster 1 (down)", "Cluster 2 (up)", "Cluster 3 (basal)"};

        System.out.println("\n--- Mean Trajectories per Cluster ---");
        for (int c = 0; c < 4; c++) {
            int start = clusterRanges[c][0], end = clusterRanges[c][1];
            System.out.printf("%n%s (genes %d-%d):%n", clusterLabels[c], start, end);

            for (int t = 0; t < numberOfTimeSeries; t++) {
                double sum = 0;
                int count = 0;
                for (int g = start; g <= end; g++) {
                    for (int r = 0; r < numberOfReplicates; r++) {
                        int sampleIdx = t * numberOfReplicates + r;
                        sum += expressionData[g][sampleIdx];
                        count++;
                    }
                }
                double mean = sum / count;
                System.out.printf("  T%2d: %7.2f%n", t, mean);
            }
        }
    }

    /**
     * Computes average pairwise Pearson correlation within each cluster.
     * Values > 0.7 suggest good within-cluster coherence; < 0.4 suggests noise dominates.
     */
    private static void printWithinClusterCorrelations(double[][] expressionData) {
        int[][] clusterRanges = {{0, 99}, {100, 199}, {200, 299}, {300, 999}};
        String[] clusterLabels = {"Cluster 0", "Cluster 1", "Cluster 2", "Cluster 3"};

        System.out.println("\n--- Average Within-Cluster Pearson Correlation ---");
        for (int c = 0; c < 4; c++) {
            int start = clusterRanges[c][0], end = clusterRanges[c][1];
            int nGenes = end - start + 1;

            // Sample up to 200 random gene pairs for efficiency
            double sumCorr = 0;
            int pairsTested = 0;
            int maxPairs = Math.min(200, nGenes * (nGenes - 1) / 2);

            while (pairsTested < maxPairs) {
                int g1 = start + rng.nextInt(nGenes);
                int g2 = start + rng.nextInt(nGenes);
                if (g1 == g2) continue;

                double corr = computePearsonCorrelation(expressionData[g1], expressionData[g2]);
                if (!Double.isNaN(corr)) {
                    sumCorr += corr;
                    pairsTested++;
                }
            }

            double avgCorr = pairsTested > 0 ? sumCorr / pairsTested : Double.NaN;
            System.out.printf("%s: r = %.3f (n=%d pairs)%n", clusterLabels[c], avgCorr, pairsTested);
        }
    }

    /**
     * Computes Pearson correlation coefficient between two expression vectors.
     */
    private static double computePearsonCorrelation(double[] x, double[] y) {
        int n = x.length;
        if (n != y.length) return Double.NaN;

        double meanX = Arrays.stream(x).average().orElse(0);
        double meanY = Arrays.stream(y).average().orElse(0);

        double num = 0, denX = 0, denY = 0;
        for (int i = 0; i < n; i++) {
            double dx = x[i] - meanX;
            double dy = y[i] - meanY;
            num += dx * dy;
            denX += dx * dx;
            denY += dy * dy;
        }

        double denom = Math.sqrt(denX * denY);
        return (denom > 1e-10) ? num / denom : 0.0;
    }

    /**
     * Basic validation checks to ensure simulation parameters produce intended behavior.
     */
    private static void validateSimulation(double[][] expressionData) {
        System.out.println("\n--- Validation Checks ---");

        // Check 1: No values below minimum
        boolean allValid = true;
        for (double[] gene : expressionData) {
            for (double val : gene) {
                if (val < minExpressionValue - 1e-6) { // small tolerance for floating point
                    allValid = false;
                    break;
                }
            }
            if (!allValid) break;
        }
        System.out.println(allValid ? "✓ All values >= minExpressionValue" : "✗ Found values below minimum");

        // Check 2: Cluster mean trends (simplified)
        int[][] clusterRanges = {{0, 99}, {100, 199}, {200, 299}, {300, 999}};
        int[] expectedDirections = {1, -1, 1, 0}; // up, down, up, flat

        for (int c = 0; c < 4; c++) {
            int start = clusterRanges[c][0], end = clusterRanges[c][1];
            double meanT0 = 0, meanT12 = 0;
            int count = 0;

            for (int g = start; g <= end; g++) {
                // Average replicates at time 0 and time 12
                for (int r = 0; r < numberOfReplicates; r++) {
                    meanT0 += expressionData[g][0 * numberOfReplicates + r];
                    meanT12 += expressionData[g][12 * numberOfReplicates + r];
                }
                count += numberOfReplicates;
            }
            meanT0 /= count;
            meanT12 /= count;

            double change = meanT12 - meanT0;
            String status;
            if (expectedDirections[c] == 1 && change > 5) {
                status = "✓ Increasing as expected";
            } else if (expectedDirections[c] == -1 && change < -5) {
                status = "✓ Decreasing as expected";
            } else if (expectedDirections[c] == 0 && Math.abs(change) < 10) {
                status = "✓ Flat as expected";
            } else {
                status = String.format("⚠ Unexpected trend: Δ=%.2f (expected %s)",
                        change, expectedDirections[c] == 1 ? "up" : expectedDirections[c] == -1 ? "down" : "flat");
            }
            System.out.printf("Cluster %d: %s%n", c, status);
        }
    }
}