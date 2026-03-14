# MBC: Modular Biological Clustering Framework

MBC is a Java-based framework designed for benchmarking and simulating clustering algorithms applied to biological data, specifically gene expression time-series data with replicates. It provides a modular architecture for data normalization, filtering, distance calculation, clustering, and performance evaluation.

## Key Features

- **Clustering Algorithms**: KMeans, DBSCAN, Fuzzy C-Means (FCM), and Hierarchical Clustering.
- **Normalization**: IRLS (Iteratively Reweighted Least Squares), Z-Score, Median Ratios, and Pseudologarithm.
- **Filtering**: Gene filtering by variance, total expression, and zero counts; sample-based filtering.
- **Distance Metrics**: Correlation, Euclidean, and Jensen-Shannon distance.
- **Benchmarking**: Support for both external (Jaccard, Accuracy, ARI, NMI) and internal (Silhouette, WCSS) metrics.
- **Modular Design**: Extensible architecture using Factory and Decorator patterns.

---

## Prerequisites

- **Java Development Kit (JDK) 25**
- **Apache Maven 3.9+**

---

## Building the Project

To compile and generate the executable JAR files, run the following command from the project root:

```bash
mvn clean package
```

This will generate two "Fat JARs" in the `mbc/target/` directory:
1. `ClusterGenerationService.jar`: For processing data and generating clusters.
2. `ClusterBenchmarkService.jar`: For evaluating clustering results.

---

## Usage: Cluster Generation

Use `ClusterGenerationService.jar` to normalize, filter, and cluster your gene expression data.

### Basic Syntax
```bash
java -jar ClusterGenerationService.jar <data_file> <metadata_file> <output_prefix> [OPTIONS]
```

### Options
- `-a, --algorithm`: `kmeans`, `hierarchical`, `dbscan`, `fcm`. (Default: `hierarchical`)
- `-k, --clusters`: Number of clusters. (Default: `10`)
- `-n, --norm`: List of normalization methods (e.g., `-n medianratios zscore`).
- `-f, --filter`: List of gene filters (e.g., `-f non-zero variance 1.0 total-expression 1.0`).
- `-fs, --filter-samples`: List of sample trait/value pairs (e.g., `-fs Condition Treatment`).
- `-d, --distance`: `correlation`, `euclidean`, `jensenshannon`.
- `-p, --profile`: Enable performance profiling (time and memory).

### Example
```bash
java -jar ClusterGenerationService.jar data.tsv metadata.csv my_results \
  --algorithm kmeans -k 12 \
  --norm medianratios zscore \
  --filter non-zero variance 1.5 \
  --profile
```

---

## Usage: Benchmarking

Use `ClusterBenchmarkService.jar` to evaluate your clustering results against a gold standard.

### Basic Syntax
```bash
java -jar ClusterBenchmarkService.jar <cluster_file> <gold_standard_file> [OPTIONS]
```

### Options
- `-p, --processed-data`: Path to the processed expression matrix (required for Silhouette/WCSS).
- `-d, --distance`: Distance metric to use for internal validation. (Default: `correlation`)

### Example
```bash
java -jar ClusterBenchmarkService.jar my_results.txt ground_truth.txt \
  --processed-data my_results_processed.csv \
  --distance correlation
```

---

## Development & Extension

MBC is built using **Interface-Driven Design**. To add a new algorithm or metric:
1. Implement the corresponding interface (e.g., `IClusteringAlgorithm` or `IClusterBenchmark`).
2. Register the new implementation in the appropriate factory class (e.g., `ClusterAlgorithmFactory`).
3. The CLI will automatically support the new implementation once registered in the factory.
