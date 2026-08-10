<img width="2157" height="1397" alt="trac-glm_logo" src="https://github.com/user-attachments/assets/094fba29-cdaa-484d-b4c4-2d2f8bcd845f" />

 # TRaC-GLM — Generalized linear models for clustering of RNA-seq time series experiments

TRaC-GLM provides a rigorous statistical framework for RNA-seq time-series clustering. Unlike standard clustering on raw or simply normalized counts, the TRaC-GLM workflow ensures that clustered trajectories represent true biological signals rather than random noise.

### 1. Generalized Linear Modeling (GLM)
The framework models gene expression using a **Negative Binomial distribution**, which accurately captures the overdispersion common in RNA-seq data. It uses an **Iteratively Reweighted Least Squares (IRLS)** engine with **Ridge Regularization** (Normal prior) to estimate stable log fold change ($\beta$) coefficients.

### 2. Wald Significance Test
For each gene's trajectory, the framework performs a **Wald Test** to evaluate the statistical significance of the estimated coefficients. It utilizes a **Sandwich Covariance Estimator** to robustly calculate standard errors under the penalized likelihood framework.

### 3. Multiple Testing Correction (FDR)
To handle the high-throughput nature of genomics (testing thousands of genes simultaneously), the framework applies the **Benjamini-Hochberg (FDR)** procedure. The correction is specifically optimized to ignore the intercept term, maximizing statistical power to detect dynamic temporal changes.

### 4. Intercept Removal for Dynamic Clustering
Biologically, clustering should be driven by how genes change over time, not their absolute starting expression levels. The pipeline automatically drops the intercept ($\beta_0$) before clustering, ensuring that the distance metrics operate strictly on the **dynamic temporal trajectories** ($\beta_1, \beta_2, \dots, \beta_n$).

## Key Features

- **Clustering Algorithms**: KMeans, DBSCAN, Fuzzy C-Means (FCM), and Hierarchical Clustering.
- **Workflows**: `trac-glm` (the statistical pipeline: median-ratio size factors, NB GLM, Wald test, FDR) or a standard pipeline with Z-Score, Median Ratios, Pseudologarithm and Count-Distribution normalizers.
- **Filtering**: Gene filtering by variance, total expression, zero counts, and **statistical significance**.
- **Distance Metrics**: Correlation, Euclidean, and Jensen-Shannon distance.
- **Benchmarking**: Support for both external (Jaccard, Accuracy, ARI, NMI) and internal (Silhouette, WCSS) metrics.
- **Modular Design**: Extensible architecture using Factory and Decorator patterns.


## Prerequisites

- **Java 25** (JRE 25 to run the JARs; **JDK 25** to build from source).
- **Apache Maven 3.9+** to build from source. The repository does **not** ship a Maven
  wrapper (`mvnw`), so Maven must be installed and on your `PATH`.

---

## Installation

You do not need to build the project from source to use it. Simply download the latest executable JAR files from the GitHub Releases page:

1. Navigate to the **Releases** section on the right side of the GitHub repository page.
2. Download the two required JAR files:
   - `ClusterGenerationService.jar`
   - `ClusterBenchmarkService.jar`

### Building from source

The Maven project lives in the **`mbc/` subdirectory**, not at the repository root:

```bash
cd mbc && mvn clean package
```

This produces `mbc/target/ClusterGenerationService.jar` and
`mbc/target/ClusterBenchmarkService.jar` (self-contained "fat" JARs).

---

## Quick Start

All commands below assume you are inside the `mbc/` directory.

```bash
java -jar target/ClusterGenerationService.jar data/data.tsv data/metadata.tsv results/demo -k 4 --filter significance,0.5
```

> **Why `--filter significance,0.5` here?** `mbc/data/` is a small 100-gene smoke-test
> slice in which 85 of the 100 genes are basal (flat) by construction. At the default
> FDR threshold of 0.05 **no gene passes**, and the run aborts with
> `No genes passed the filter.` Use the full 1000-gene benchmark dataset (see
> [Reproducing the benchmark dataset](#reproducing-the-benchmark-dataset)) for a
> realistic run at the default threshold.

---

## Usage: Cluster Generation

Use `ClusterGenerationService` to normalize, filter, and cluster your gene expression data.

### Basic Syntax
```bash
java -jar ClusterGenerationService.jar <data_file> <metadata_file> <output_prefix> [OPTIONS]
```

### Positional Parameters
- `0` (`data_file`): Path to the count matrix file (`.csv`, `.tsv` or `.txt`).
- `1` (`metadata_file`): Path to the metadata file (`.csv`, `.tsv` or `.txt`).
- `2` (`output_prefix`): Destination base path. The extension is added automatically —
  the tool writes `<output_prefix>.txt` and `<output_prefix>_normalized_data.csv`.

### Options
- `-a`, `--algorithm`: Clustering method (`kmeans`, `hierarchical`, `dbscan`, `fcm`). **Default**: `hierarchical`
- `-k`, `--clusters`: Number of expected trajectories (k). Must be between 1 and the number of genes that survive filtering. Ignored by `dbscan`, which discovers its own cluster count. **Default**: `10`
- `--eps`: Epsilon parameter for DBSCAN. **Default**: `0.5`
- `--minPts`: Minimum points parameter for DBSCAN (the point itself counts towards this total). **Default**: `5`
- `-n`, `--norm`: Workflow / normalization selector, comma-separated (`trac-glm`, `zscore`, `median`, `pseudolog`, `countdist`). **Default**: `trac-glm`. ⚠️ **This flag selects the workflow — see the warning below.**
- `-f`, `--filter`: List of filters to apply, comma-separated. Supported: `non-zero`, `variance`, `total-expression`, `significance`. See [Filtering](#filtering).
- `-fs`, `--filter-samples`: List of sample trait/value pairs to include, comma-separated (e.g. `--filter-samples Replicate,1`).
- `-c`, `--compress`: Replicate compression method (`mean`, `variance`, `default`). Only used by the standard workflow. **Default**: `default`
- `-d`, `--distance`: Distance metric (`correlation`, `euclidean`, `jensenshannon`). **Default**: `correlation`
- `-l`, `--linkage`: Linkage criterion for hierarchical clustering (`average`, `complete`, `single`). **Default**: `average`
- `-p`, `--profile`: Enable profiling. Records execution time, peak heap memory, input vs. clustered gene/sample counts, and the list of **filtered-out** genes with the reason each was dropped, into `profile_metrics.tsv` in the current working directory.
- `-h`, `--help` / `-V`, `--version`: Standard help and version output.

### ⚠️ `--norm` selects the workflow, not just the normalization

This is the single most important thing to know about the CLI. TRaC-GLM has **two
distinct workflows**, and `-n` chooses between them:

| `-n` value | Workflow | What happens |
|---|---|---|
| omitted, or contains `trac-glm` (any case) | **TRaC-GLM** | Median-ratio size factors → NB GLM → Wald test → BH-FDR → significance filter → intercept removal → clustering. Output has **K+1** columns. |
| any other value (`zscore`, `median`, `pseudolog`, `countdist`) | **Standard** | Filter → replicate compression → the requested normalizers → clustering. **No GLM, no Wald test, no FDR correction, and the significance filter is never applied.** Output has **K** columns. |

If you pass `-n zscore` expecting "the same pipeline with a different normalization",
you will silently bypass the entire statistical method this tool implements. Use the
default (or `-n trac-glm`) for the TRaC-GLM workflow.

`trac-glm` names the workflow, not a normalizer — the GLM's own size factors are always
median-ratio. It cannot be combined with the standard normalizers: in a list such as
`-n trac-glm,zscore` the TRaC-GLM workflow wins and the other entries are ignored.

### Example

Using the regenerated 1000-gene benchmark dataset (see
[Reproducing the benchmark dataset](#reproducing-the-benchmark-dataset)):

```bash
java -jar ClusterGenerationService.jar data/simulated/data.tsv data/simulated/metadata.tsv results/clusters_out \
  -a kmeans -k 4 \
  -d correlation \
  --filter non-zero,significance,0.05 \
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
- `0` (`cluster_file`): Path to the clustering results produced by `ClusterGenerationService` (i.e. `<output_prefix>.txt`).
- `1` (`gold_standard_file`): Path to the gold standard (ground truth) cluster file.

> **Both files are parsed as tab-separated, regardless of their extension.** A genuinely
> comma-separated cluster file will not load.

### Options
- `-nd`, `--normalized-data`: Path to the normalized matrix `<output_prefix>_normalized_data.csv` produced by `ClusterGenerationService`. Required to compute the internal metrics (Silhouette and WCSS); external metrics are computed without it.
- `-d`, `--distance`: Distance metric (`correlation`, `euclidean`, `jensenshannon`). **Default**: `correlation`

### Example
```bash
java -jar ClusterBenchmarkService.jar results/clusters_out.txt data/simulated/ground_truth.txt \
  -nd results/clusters_out_normalized_data.csv \
  -d correlation
```

Genes present in the cluster file but absent from the normalized matrix — the basal
genes removed by filtering — are skipped by the internal metrics and scored `0.0` in the
per-gene column.

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

> A threshold-taking filter whose threshold is missing (for example a trailing
> `--filter variance`) is silently ignored rather than reported as an error.

#### `non-zero`
Removes any gene that contains **at least one zero count** across all samples. No parameter required.

- **Use case**: Eliminates genes with dropout events (common in RNA-seq) that can distort model fitting and distance calculations.
- **Stage**: Pre-normalization.
- **Example**: `--filter non-zero`

#### `variance <threshold>`
Computes the **sample variance** (n−1 denominator) of a gene's expression across all samples. A gene is **kept** only if its variance is strictly greater than the threshold.

- **Use case**: Filters out flat or near-constant genes that carry no dynamic temporal information.
- **Stage**: Pre-normalization.
- **Example**: `--filter variance,1.0` — keeps genes with variance > 1.0.

#### `total-expression <threshold>`
Sums all expression values for a gene across every sample. A gene is **kept** only if its total is strictly greater than the threshold.

- **Use case**: Removes very lowly expressed genes that are likely biological noise or artifacts of sequencing depth.
- **Stage**: Pre-normalization.
- **Example**: `--filter total-expression,10` — keeps genes with total counts > 10.

#### `significance <threshold>`
Applied **after** GLM fitting, Wald testing, and Benjamini-Hochberg FDR correction. A gene is **kept** if **at least one** adjusted p-value across its time-point coefficients is strictly below the threshold. The intercept is excluded from this test.

- **Use case**: Ensures only genes with statistically significant temporal trajectories enter the clustering step. This is the core statistical filter of the TRaC-GLM pipeline.
- **Stage**: Post-normalization.
- **Default**: Always applied with alpha = **0.05**. If you pass `--filter significance,<value>`, your threshold replaces the default.
- **Example**: `--filter significance,0.1` — uses a relaxed threshold of 0.1 instead of the default 0.05.

### Combining Multiple Filters

Filters can be **chained** in a single `--filter` flag. They are applied conjunctively (a gene must pass **all** filters to be retained):

```bash
--filter non-zero,variance,1.0,total-expression,10,significance,0.05
```

This keeps genes that have no zero counts, a variance > 1.0, a total expression > 10
*(all pre-normalization)*, and at least one FDR-adjusted p-value < 0.05
*(post-normalization)*.

### Sample Filters

The `--filter-samples` (`-fs`) flag filters **samples** (columns) by metadata traits before any gene filtering or normalization. It takes comma-separated trait/value pairs, and a sample is kept only if it matches **every** constraint.

Trait names must match the column headers of your own metadata file. For a metadata file
carrying `Condition` and `Tissue` columns, this keeps only samples where
`Condition = Treatment` **and** `Tissue = Liver`:

```bash
--filter-samples Condition,Treatment,Tissue,Liver
```

> The bundled datasets carry only `Sample`, `Replicate` and `Time`, so there is no
> biologically meaningful sample filter to demonstrate on them.

Two constraints apply to any sample filter used with the TRaC-GLM workflow:

- **Do not reduce the design to one replicate per time point** (for example
  `--filter-samples Replicate,1`). Dispersion is estimated from within-time-point
  replicate variance, which is undefined without replication; the run will usually end
  with `No genes passed the filter.`
- **Do not remove every sample of a time point.** That leaves the corresponding design
  matrix column empty and the fit degenerate.

### Default Behavior

- The **significance filter** (alpha = 0.05) is **always applied** in the TRaC-GLM workflow after GLM fitting and FDR correction, even when `--filter` is omitted. To use a different threshold, pass `--filter significance,<value>`.
- **When `--filter` is omitted**, no pre-normalization gene filters are applied — all genes proceed to the GLM step, but only statistically significant ones enter clustering.
- If **no** gene passes a filter, the run aborts with `No genes passed the filter.`

---

## Input & Output Formats

> **Note**: A small demo dataset (100 genes) ships in `mbc/data/`. The 1000-gene dataset
> used for the published benchmarks is regenerated with the bundled simulator — see
> [Reproducing the benchmark dataset](#reproducing-the-benchmark-dataset).

File format is detected from the extension: `.csv` is comma-separated; `.tsv` and `.txt`
are tab-separated.

### 1. Data Input

`ClusterGenerationService` requires two files: a count matrix and a metadata file.

**Count Matrix (`data.tsv`)**
The first column contains Gene IDs; subsequent columns contain expression values (raw counts) for each sample. Column headers after the first must match the sample IDs in the metadata file.
```tsv
GeneId	R1T0	R2T0	R3T0	R1T1
GENE1	120	131	118	205
GENE2	54	61	57	52
GENE3	1002	1105	1050	1200
```

**Metadata (`metadata.tsv`)**
Maps samples to their experimental conditions. It **must** contain `Sample` and `Time` columns; any additional columns become filterable traits. `Time` must be numeric — values are sorted ascending and mapped to dense time indices.
```tsv
Sample	Replicate	Time
R1T0	1	0
R2T0	2	0
R3T0	3	0
R1T1	1	1
```

### 2. Cluster Results (TSV, written as `<output_prefix>.txt`)

Generated by `ClusterGenerationService` and used as input for `ClusterBenchmarkService`. Each row contains a Gene ID followed by membership values for each cluster.

In the TRaC-GLM workflow the output contains **K+1 columns**: K trajectories plus one column for the basal (filtered-out) genes. Genes that passed filtering carry their membership in columns 1–K and `0.0` in the last column; filtered-out genes carry `0.0` in columns 1–K and `1.0` in the last.

The extra basal column is only added when at least one gene was actually filtered out;
if nothing was filtered the file has exactly K columns. The standard workflow (`-n zscore`
and friends) never adds it.

**Example** (K=2, plus the basal column)
```tsv
GENE1	1.0	0.0	0.0	
GENE2	0.0	1.0	0.0	
GENE3	1.0	0.0	0.0	
GENE4	0.0	0.0	1.0	
GENE5	0.0	0.0	1.0	
```

Hard-assignment algorithms emit `1.0` in exactly one column. Fuzzy C-Means emits
fractional memberships that sum to 1.0; every metric resolves a gene's label by taking
the **highest** membership value.

### 3. Benchmark Output (TSV)

Generated by `ClusterBenchmarkService`. The output file shares the base name of the input clusters file, suffixed with `_benchmarks.tsv`.

Results use a three-column format: `benchmark`, `geneid`, and `value`. Global values use the literal geneid `global`; Silhouette additionally emits one row per gene.

**Example `clusters_out_benchmarks.tsv`**
```tsv
benchmark	geneid	value
jaccard	global	0.7117256317689531
accuracy	global	0.801
adjustedrandindex	global	0.6820170068845588
nmi	global	0.6306430992272899
silhouette	global	0.5655285659309711
silhouette	GENE1	0.5076034705180335
silhouette	GENE2	0.7347867266611773
silhouette	GENE3	0.47599069690619505
wcss	global	47.06344819318222
```

Note the metric identifiers are lowercased enum names — in particular
`adjustedrandindex` (no underscores). `silhouette` and `wcss` appear only when `-nd` is
supplied.

### 4. Profile Report (TSV)

Generated by `ClusterGenerationService` when the `--profile` (`-p`) flag is enabled. The report is written to `profile_metrics.tsv` **in the current working directory** (not next to the output prefix), overwriting any existing file, in a three-column format: `Category`, `Metric`, and `Value`.

**Example `profile_metrics.tsv`**
```tsv
Category	Metric	Value
profiling_metrics	input_genes	1000
profiling_metrics	input_samples	39
profiling_metrics	clustered_genes	399
profiling_metrics	time_series	12
profiling_metrics	execution_time_seconds	2.2425
profiling_metrics	peak_heap_memory_mb	62.40
filtered_out_genes	GENE301	SIGNIFICANCE_FILTER
filtered_out_genes	GENE302	SIGNIFICANCE_FILTER
filtered_out_genes	GENE303	SIGNIFICANCE_FILTER
```

- The gene list under `filtered_out_genes` names the genes that were **discarded**, each with the filter that rejected it (`ZERO_FILTER`, `VARIANCE_FILTER`, `TOTAL_EXPRESSION_FILTER`, `SIGNIFICANCE_FILTER`). Retained genes are not listed, and no sample IDs are recorded.
- `time_series` counts the coefficient columns used for clustering, which is one fewer than the number of input time points because the intercept is dropped (12 for a 13-time-point design).
- `peak_heap_memory_mb` sums the peak usage of every heap pool, so it is an upper bound rather than a simultaneous peak.

---

## Reproducing the benchmark dataset

The 1000-gene dataset behind the published benchmarks is produced by the bundled
simulator, which is fully deterministic (seed 42) and regenerates the dataset
byte-for-byte:

```bash
cd mbc && mvn clean package && java -cp target/classes org.example.SimulateDataGeneratorService ./data/simulated
```

This writes `data.tsv`, `metadata.tsv` and `ground_truth.txt`: 1000 genes, 3 replicates
across 13 time points (39 samples), in four ground-truth classes — early-response,
sigmoidal and descending trajectories (100 genes each) plus 700 basal genes.

End-to-end reproduction:

```bash
java -jar target/ClusterGenerationService.jar data/simulated/data.tsv data/simulated/metadata.tsv results/bench -k 4 -p
java -jar target/ClusterBenchmarkService.jar results/bench.txt data/simulated/ground_truth.txt -nd results/bench_normalized_data.csv
```

With the default settings this retains 399 of the 1000 genes as significant, and scores
accuracy 0.801 / ARI 0.682 against the ground truth with `-a hierarchical -k 4`.

---

## Algorithm notes and limitations

Please read these before interpreting results.

- **KMeans and Fuzzy C-Means are not reproducible across runs.** Their centroid
  initialization uses an unseeded random generator and there is currently no `--seed`
  option. Hierarchical clustering is deterministic.
- **DBSCAN ignores `-k`** and discovers its own cluster count from `--eps`/`--minPts`.
  Genes classified as noise are **omitted from the output file entirely** — they appear
  in neither a trajectory cluster nor the basal column.
- **Distance metrics on GLM betas.** In the TRaC-GLM workflow clustering operates on
  log fold-change coefficients, which are frequently negative. `correlation` and
  `euclidean` are appropriate; `jensenshannon` is defined only for non-negative
  profiles and will reject beta input with an explicit error.
- **NMI** is normalized by the maximum of the two entropies, `I(U;V) / max(H(U), H(V))`.
  Tools that normalize by the arithmetic mean (such as scikit-learn's default) will
  report slightly different values.
- **ARI** returns `1.0` when fewer than two genes are shared between the prediction and
  the gold standard, or when the denominator vanishes.
- **Hierarchical clustering is O(n³)** in the number of genes, since every pairwise
  linkage is recomputed after each merge. It is the slowest option on large gene sets.
- **Samples present in the count matrix but missing from the metadata** are silently
  assigned to the first time point. Make sure the two files agree.

---

## Development & Extension

TRaC-GLM is built using **Interface-Driven Design**. To add a new algorithm or metric:
1. Implement the corresponding interface (e.g., `IClusteringAlgorithm` or `IClusterBenchmark`).
2. Register the new implementation in the appropriate factory class (e.g., `ClusterAlgorithmFactory`).
3. The CLI will automatically support the new implementation once registered in the factory.

---

## License

Released under the MIT License. See [LICENSE](LICENSE).

## Citation

If you use TRaC-GLM in your research, please cite:

```bibtex
@software{tracglm,
  title  = {TRaC-GLM: Generalized linear models for clustering of RNA-seq time series experiments},
  author = {Medina, J. A.},
  year   = {2026},
  url    = {https://github.com/jamedinac/mbc}
}
```

<!-- TODO before submission: replace the software entry above with the published paper
     reference (journal, volume, DOI) and confirm the author list. -->
