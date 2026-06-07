# Architectural Implementation Plan: Wald Test & FDR Correction

### 1. Architecture Diagram & Class Hierarchy

To maintain the Interface-Driven Design and decouple the mathematical operations from the statistical definitions, we will introduce three new abstraction layers: Matrix Operations, Hypothesis Testing, and Multiple Testing Correction.

```text
mbc.statistics
│
├── Interfaces
│   ├── IMatrixOperations.java         // Abstraction for matrix inversion and multiplication
│   ├── IHypothesisTest.java           // Abstraction for calculating SE, Stats, and P-values
│   └── IMultipleTestingCorrection.java// Abstraction for P-value adjustment (FDR)
│
├── Implementations
│   ├── EJMLMatrixOperations.java      // EJML-based implementation for fast small-matrix algebra
│   ├── WaldTest.java                  // Implements IHypothesisTest (calculates Wald Z and P)
│   └── BenjaminiHochberg.java         // Implements IMultipleTestingCorrection
│
├── Factories
│   ├── HypothesisTestFactory.java
│   └── MultipleTestingCorrectionFactory.java
│
└── Models
    ├── IrlsFitResult.java             // Encapsulates Beta, H_unpenalized, H_penalized per gene
    └── SignificanceMetrics.java       // Encapsulates SE, Z, P-value, and Q-value (FDR)
```

#### Key Method Signatures & Data Structures
```java
// Models
public record IrlsFitResult(double[] beta, double[][] hUnpenalized, double[][] hPenalized) {}
public record SignificanceMetrics(double[] standardErrors, double[] waldStatistics, double[] pValues, double[] qValues) {}

// Interfaces
public interface IMatrixOperations {
    // Calculates: (H_penalized)^-1 * (H_unpenalized) * (H_penalized)^-1
    double[][] calculateCovarianceMatrix(double[][] hUnpenalized, double[][] hPenalized);
}

public interface IHypothesisTest {
    // Returns SE, Z, and P-values for a single gene across time points
    SignificanceMetrics testSignificance(IrlsFitResult fitResult, IMatrixOperations mathOps);
}

public interface IMultipleTestingCorrection {
    // Adjusts a 1D array of p-values using the specific methodology (e.g., BH)
    double[] adjustPValues(double[] pValues);
}
```

### 2. Libraries Evaluation & Recommendation

**Recommendation:** **Hybrid Approach (EJML + Native/Apache Commons)**

*   **Linear Algebra (Matrix Inversion/Multiplication):** Use **EJML (Efficient Java Matrix Library)**. 
    *   *Why:* EJML is highly optimized for small matrices ($P \times P$, where $P$ is the number of time points). It allows for in-place matrix operations (zero-allocation), which is critical for dodging the Garbage Collector when iterating over thousands of genes. Apache Commons Math is heavier and creates more intermediate objects during matrix decomposition.
*   **Statistical Functions (CDF $\Phi$):** 
    *   *Option A (Native):* Since you only need the standard normal CDF, implementing the Abramowitz and Stegun approximation (or the `erfc` function) natively takes ~10 lines of code. This avoids adding a massive dependency just for one math function.
    *   *Option B (Apache Commons Math):* If the project already uses Apache Commons Math elsewhere, utilize `NormalDistribution(0, 1).cumulativeProbability(z)`. 

### 3. Data Flow (Pipeline)

1.  **IRLS Convergence:** The `IRLS` class converges for gene $i$. Instead of just returning a `double[] beta`, it packages the final state into an `IrlsFitResult(beta, hUnpenalized, hPenalized)`.
2.  **Covariance Calculation:** The `WaldTest` class receives the `IrlsFitResult` and delegates matrix computation to `IMatrixOperations`. The math layer inverts $H_{penalized}$, multiplies by $H_{unpenalized}$, and multiplies by $H_{penalized}^{-1}$ to yield the Covariance matrix $\Sigma_i$.
3.  **Statistic Derivation:** `WaldTest` extracts the square root of the diagonal of $\Sigma_i$ to get the Standard Error ($SE$) for each time point coefficient. It calculates the Wald statistic $Z = \beta / SE$ and the two-tailed P-value.
4.  **Global Aggregation:** The pipeline iterates through all $N$ genes, collecting all $N \times (P-1)$ P-values (excluding the intercept) into a single flat `double[]` array.
5.  **FDR Correction:** The flat array of P-values is passed to `BenjaminiHochberg.adjustPValues()`. This class sorts the P-values, calculates the Q-values ($Q = P \cdot \frac{m}{k}$), enforces monotonicity, and restores them to their original order.
6.  **Result Integration:** The Q-values are mapped back to their respective genes, and the final `SignificanceMetrics` objects are attached to the overarching `GeneExpressionData` or `WorkflowResult`.

### 4. Memory and Performance Considerations (Java Specifics)

Processing thousands of genes in a genomic pipeline makes the JVM Garbage Collector (GC) the primary bottleneck.

*   **Pre-allocated Matrix Workspaces (Crucial):** Do not instantiate new `double[][]` or EJML `SimpleMatrix` objects inside the gene loop. Instantiate the required matrices once at the pipeline level (e.g., `workspaceH_penalized`, `workspaceH_unpenalized`, `workspaceCovariance`) and overwrite their internal data arrays (`.data` in EJML) for each gene. This achieves **zero-allocation** matrix inversion.
*   **Primitive Arrays over Objects:** Avoid `Double[]` or `List<Double>`. Use primitive `double[]` for all statistical outputs. Boxed primitives will cause severe memory bloat and GC pauses.
*   **FDR Sorting Strategy:** To perform Benjamini-Hochberg, you must sort P-values but retain their original indices to map the adjusted Q-values back to the correct genes. 
    *   *Do not* create a custom `class PValueIndex(double p, int index)` to sort using `Collections.sort()` or Java Streams. 
    *   *Instead*, use an index-sorting approach: Maintain a `double[] pValues` and an `int[] indices = {0, 1, 2, ..., N}`. Implement a quicksort that sorts the `indices` array based on the values in the `pValues` array.
*   **Parallelization:** The Wald test calculation (Steps 1-3 in Data Flow) is "Embarrassingly Parallel" per gene. You can use `IntStream.range(0, numGenes).parallel().forEach(...)` assuming the pre-allocated workspaces are made ThreadLocal (`ThreadLocal<IMatrixOperations>`) so threads don't overwrite each other's matrices. The FDR adjustment (Step 4-5) must be done sequentially after a synchronization barrier.