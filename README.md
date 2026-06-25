<img width="2157" height="1397" alt="trac-glm_logo" src="https://github.com/user-attachments/assets/094fba29-cdaa-484d-b4c4-2d2f8bcd845f" />

 # Generalized linear models for clustering of RNA-seq time series experiments 

TRaC-GLM provides a rigorous statistical framework for RNA-seq time-series clustering. Unlike standard clustering on raw or simply normalized counts, the Trac-GLM workflow ensures that clustered trajectories represent true biological signals rather than random noise.

### 1. Generalized Linear Modeling (GLM)
The framework models gene expression using a **Negative Binomial distribution**, which accurately captures the overdispersion common in RNA-seq data. It uses an **Iteratively Reweighted Least Squares (IRLS)** engine with **Ridge Regularization** (Normal prior) to estimate stable log2 fold change ($\beta$) coefficients.

### 2. Wald Significance Test
For each gene's trajectory, the framework performs a **Wald Test** to evaluate the statistical significance of the estimated coefficients. It utilizes a **Sandwich Covariance Estimator** to robustly calculate standard errors under the penalized likelihood framework.

### 3. Multiple Testing Correction (FDR)
To handle the high-throughput nature of genomics (testing thousands of genes simultaneously), the framework applies the **Benjamini-Hochberg (FDR)** procedure. The correction is specifically optimized to ignore the intercept term, maximizing statistical power to detect dynamic temporal changes.

### 4. Intercept Removal for Dynamic Clustering
Biologically, clustering should be driven by how genes change over time, not their absolute starting expression levels. The pipeline automatically drops the intercept ($\beta_0$) before clustering, ensuring that the distance metrics operate strictly on the **dynamic temporal trajectories** ($\beta_1, \beta_2, \dots, \beta_n$).

## Key Features

- **Clustering Algorithms**: KMeans, DBSCAN, Fuzzy C-Means (FCM), and Hierarchical Clustering.
- **Normalization**: IRLS (Iteratively Reweighted Least Squares), Z-Score, Median Ratios, and Pseudologarithm.
- **Filtering**: Gene filtering by variance, total expression, zero counts, and **statistical significance**.
- **Distance Metrics**: Correlation, Euclidean, and Jensen-Shannon distance.
- **Benchmarking**: Support for both external (Jaccard, Accuracy, ARI, NMI) and internal (Silhouette, WCSS) metrics.
- **Modular Design**: Extensible architecture using Factory and Decorator patterns.


## Prerequisites

- **Java Runtime Environment (JRE) 25** or higher to run the executable JAR files.

---

## Installation

You do not need to build the project from source to use it. Simply download the latest executable JAR files from the GitHub Releases page:

1. Navigate to the **Releases** section on the right side of the GitHub repository page.
2. Download the two required JAR files:
   - `ClusterGenerationService.jar`
   - `ClusterBenchmarkService.jar`

*(Note: If you wish to build the project from source, you will need **JDK 25** and Apache Maven 3.9+. Run `mvn clean package` in the project root to generate the Fat JARs).*

---

## Usage: Cluster Generation

Use `ClusterGenerationService` to normalize, filter, and cluster your gene expression data.

### Basic Syntax
```bash
java -jar ClusterGenerationService.jar <data_file> <metadata_file> <output_prefix> [OPTIONS]
```

### Positional Parameters
- `0` (`data_file`): Path to the count matrix file (e.g., `counts.csv`).
- `1` (`metadata_file`): Path to the metadata file (e.g., `metadata.csv`).
- `2` (`output_prefix`): Destination base path/filename for the clustering results.

### Options
- `-a`, `--algorithm`: Clustering method (`kmeans`, `hierarchical`, `dbscan`, `fcm`). **Default**: `hierarchical`
- `-k`, `--clusters`: Number of clusters (k) for the genes that pass filtering. Genes that are filtered out (e.g., non-significant) are not counted toward this value. **Default**: `10`
- `--eps`: Epsilon parameter for DBSCAN. **Default**: `0.5`
- `--minPts`: Minimum points parameter for DBSCAN. **Default**: `5`
- `-n`, `--norm`: Normalization methods to apply in order, comma-separated (`irls`, `zscore`, `median`, `pseudolog`, `countdist`). **Default**: `irls`
- `-f`, `--filter`: List of filters to apply, comma-separated. Supported: `non-zero`, `variance`, `total-expression`, `significance` (e.g., `--filter non-zero,variance,0.5,significance,0.05`). 
- `-fs`, `--filter-samples`: List of sample traits to include, comma-separated (e.g., `--filter-samples Condition,Treatment,Tissue,Liver`). 
- `-c`, `--compress`: Replicate compression method (`mean`, `variance`, `default`). **Default**: `default`
- `-d`, `--distance`: Distance metric (`correlation`, `euclidean`, `jensenshannon`). **Default**: `correlation`
- `-l`, `--linkage`: Linkage criterion for hierarchical clustering (`average`, `complete`, `single`). **Default**: `average`
- `-p`, `--profile`: Flag to enable profiling. Records execution time, peak memory usage, input vs. clustered gene/sample counts, and lists all retained gene and sample IDs in `profile_metrics.txt`.
- `-nf`, `--no-filter`: Flag to disable all gene filtering. This overrides the default filters and the --filter flag.

### Example
```bash
java -jar ClusterGenerationService.jar data/counts.csv data/metadata.csv results/clusters_out \
  -a kmeans -k 5 \
  -n irls \
  -d correlation \
  --filter non-zero,variance,1.0,significance,0.05 \
  --profile
```

---

## Usage: Benchmarking

Use `ClusterBenchmarkService` to evaluate your clustering results against a gold standard and optionally calculate internal metrics.

### Basic Syntax
```bash
java -jar ClusterBenchmarkService.jar <cluster_file> <gold_standard_file> [OPTIONS]
```

### Positional Parameters
- `0` (`cluster_file`): Path to the generated clustering results file.
- `1` (`gold_standard_file`): Path to the gold standard (ground truth) cluster file.

### Options
- `-p`, `--processed-data`: Path to the processed gene expression matrix (CSV). This file is generated by `ClusterGenerationService` and is required for calculating internal metrics (like WCSS and Silhouette).
- `-d`, `--distance`: Distance metric (`correlation`, `euclidean`, `jensenshannon`). **Default**: `correlation`

### Example
```bash
java -jar ClusterBenchmarkService.jar results/clusters_out.csv data/gold_standard.csv \
  -p results/clusters_out_processed.csv \
  -d correlation
```

---

## Filtering

TRaC-GLM applies filters in a **two-stage pipeline** that is tightly coupled with the statistical modeling workflow. Filters are partitioned based on *when* they execute relative to normalization and model fitting:

```
Raw Counts ──▶ [Pre-Normalization Filters] ──▶ GLM / Normalization ──▶ [Post-Normalization Filters] ──▶ Clustering
```

### Two-Stage Pipeline

| Stage | Filters | Operates on | Purpose |
|---|---|---|---|
| **Pre-normalization** | `non-zero`, `variance`, `total-expression` | Raw expression counts | Remove low-quality or uninformative genes **before** statistical modeling |
| **Post-normalization** | `significance` | FDR-adjusted p-values | Retain only genes with statistically significant temporal changes **after** GLM fitting |

Pre-normalization filters reduce the gene set early, improving computational efficiency and preventing noisy genes from affecting the GLM estimates. The post-normalization `significance` filter acts as a statistical gatekeeper, ensuring only biologically meaningful trajectories enter the clustering step.

### Gene Filters

All gene filters are specified via the `--filter` (`-f`) flag as a comma-separated list. Filters that require a threshold take it as the next value in the list.

#### `non-zero`
Removes any gene that contains **at least one zero count** across all samples. No parameter required.

- **Use case**: Eliminates genes with dropout events (common in RNA-seq) that can distort model fitting and distance calculations.
- **Stage**: Pre-normalization.
- **Example**: `--filter non-zero`

#### `variance <threshold>`
Computes the **sample variance** of a gene's expression across all samples and removes genes whose variance is **at or below** the threshold.

- **Use case**: Filters out flat or near-constant genes that carry no dynamic temporal information — these genes would add noise to clustering without contributing meaningful signal.
- **Stage**: Pre-normalization.
- **Example**: `--filter variance,1.0` — removes genes with variance ≤ 1.0.

#### `total-expression <threshold>`
Sums all expression values for a gene across every sample and removes genes whose total is **at or below** the threshold.

- **Use case**: Removes very lowly expressed genes that are likely biological noise or artifacts of sequencing depth.
- **Stage**: Pre-normalization.
- **Example**: `--filter total-expression,10` — removes genes with total counts ≤ 10.

#### `significance <threshold>`
Applied **after** GLM fitting, Wald testing, and Benjamini-Hochberg FDR correction. Retains genes where **at least one** adjusted p-value across time-point coefficients is below the threshold.

- **Use case**: Ensures only genes with statistically significant temporal trajectories (i.e., genes that truly change over time) enter the clustering step. This is the core statistical filter of the TRaC-GLM pipeline.
- **Stage**: Post-normalization.
- **Example**: `--filter significance,0.05` — retains genes with at least one FDR-adjusted p-value < 0.05.

### Combining Multiple Filters

Filters can be **chained** in a single `--filter` flag. They are applied conjunctively (a gene must pass **all** filters to be retained):

```bash
--filter non-zero,variance,1.0,total-expression,10,significance,0.05
```

This applies, in order:
1. `non-zero` — remove genes with any zero count *(pre-normalization)*
2. `variance > 1.0` — remove low-variance genes *(pre-normalization)*
3. `total-expression > 10` — remove lowly expressed genes *(pre-normalization)*
4. `significance < 0.05` — remove non-significant genes *(post-normalization)*

### Sample Filters

The `--filter-samples` (`-fs`) flag filters **samples** (columns) by metadata traits before any gene filtering or normalization. It takes comma-separated trait-value pairs:

```bash
--filter-samples Condition,Treatment,Tissue,Liver
```

This keeps only samples where `Condition = Treatment` **AND** `Tissue = Liver`. All specified trait constraints must be satisfied for a sample to be included.

### Default Behavior & `--no-filter`

- **When `--filter` is omitted**, no gene filters are applied — all genes proceed through the full pipeline.
- **`--no-filter` (`-nf`)** explicitly disables all gene filtering. This flag **overrides** any `--filter` values, ensuring no genes are removed regardless of other arguments.

### Examples

**Strict filtering** — remove zeros, low-variance genes, and keep only statistically significant trajectories:
```bash
java -jar ClusterGenerationService.jar counts.csv metadata.csv results/out \
  --filter non-zero,variance,1.0,significance,0.05 \
  -a kmeans -k 5
```

**Significance-only filtering** — rely solely on the statistical test with a relaxed threshold:
```bash
java -jar ClusterGenerationService.jar counts.csv metadata.csv results/out \
  --filter significance,0.1
```

**Combined sample and gene filtering** — subset to a specific condition and filter genes:
```bash
java -jar ClusterGenerationService.jar counts.csv metadata.csv results/out \
  --filter-samples Condition,Treatment \
  --filter non-zero,significance,0.05 \
  -a hierarchical -k 8
```

---

## Input & Output Formats

>**Note**: Simulated sample data, metadata description, and ground truth clustering can be found at `./data` directory

### 1. Data Input (CSV/TSV)

`ClusterGenerationService` requires two main files: a count matrix and a metadata file.

**Count Matrix (`counts.csv`)**
A CSV file where the first column contains Gene IDs and subsequent columns contain expression values (typically raw counts) for each sample.
```csv
Gene,Sample1,Sample2,Sample3,Sample4
G1,10,20,15,25
G2,5,6,5,7
G3,100,110,105,120
```

**Metadata (`metadata.csv`)**
A CSV file mapping samples to their experimental conditions. It **must** contain `Sample` and `Time` columns.
```csv
Sample,Time,Condition
Sample1,0.0,Control
Sample2,1.0,Control
Sample3,2.0,Control
Sample4,3.0,Control
```

### 2. Cluster Results (TSV)

Generated by `ClusterGenerationService` and used as input for `ClusterBenchmarkService`. Each row contains a Gene ID followed by membership probabilities for each cluster.

**Example `clusters.txt`**
```tsv
G1	1.0	0.0	
G2	1.0	0.0	
G3	0.0	1.0	
```

### 3. Benchmark Reports (TXT)

The output of `ClusterBenchmarkService` provides a summary of all metrics and detailed gene-level statistics where applicable.

**Example `benchmarks.txt`**
```text
=== BENCHMARK SUMMARY ===
Jaccard:	0.85
ARI:	0.78
Silhouette:	0.42

=== Jaccard ===
Global Value:	0.85

=== Silhouette ===
Global Value:	0.42

Gene ID	Silhouette Value
G1	0.45
G2	0.38
G3	0.43
```

### 4. Profile Report (TXT)

Generated by `ClusterGenerationService` when the `--profile` (`-p`) flag is enabled. The report is saved as `profile_metrics.txt` in the working directory and contains input vs. clustered dimensions, performance metrics, and a full list of retained gene IDs.

**Example `profile_metrics.txt`**
```text
--- Profiling Results ---
Input Genes: 1000
Input Samples: 39
Clustered Genes: 476
Time Series: 12
Execution Time: 12.0006 seconds
Peak Heap Memory Usage: 60.35 MB
-------------------------

--- Retained Genes ---
GENE1
GENE2
GENE3
...
```

---

## Development & Extension

MBC is built using **Interface-Driven Design**. To add a new algorithm or metric:
1. Implement the corresponding interface (e.g., `IClusteringAlgorithm` or `IClusterBenchmark`).
2. Register the new implementation in the appropriate factory class (e.g., `ClusterAlgorithmFactory`).
3. The CLI will automatically support the new implementation once registered in the factory.
