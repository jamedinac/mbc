# TracGLMWorkflow Implementation Plan

## Objective
Implement a new clustering workflow (`TracGLMWorkflow`) that leverages a Generalized Linear Model (GLM) for estimating log2 fold changes ($\beta$ coefficients), evaluates statistical significance using the Wald Test, corrects for multiple testing via the Benjamini-Hochberg (FDR) procedure, and filters genes prior to clustering.

## Step 1: Core Interfaces and Data Transfer Objects
Create the foundational structures to support the new pipeline without breaking existing normalizers.

- **`GLMFitResult` (Record)**
  - Path: `mbc/src/main/java/Common/GLMFitResult.java`
  - Purpose: Carries all statistical state out of the GLM Processor.
  - Fields: `double[][] betas`, `double[][] weights`, `double[] priorVariance`, `double[][] designMatrix`, `double[] alphas`.

- **`IModelFitter` (Interface)**
  - Path: `mbc/src/main/java/Interfaces/IModelFitter.java`
  - Purpose: Defines the contract for fitting a statistical model.
  - Method: `GLMFitResult fitModel(double[][] normalizedData, int[] replicatesPerTime, int[] sampleTimeMap, int numberOfTimeSeries);`

- **`ISignificanceTester` (Interface)**
  - Path: `mbc/src/main/java/Interfaces/ISignificanceTester.java`
  - Purpose: Computes raw p-values and Z-scores from the GLM result.
  - Method: `double[][] calculateRawPValues(GLMFitResult glmFit);`

- **`IMultipleTestingCorrection` (Interface)**
  - Path: `mbc/src/main/java/Interfaces/IMultipleTestingCorrection.java`
  - Purpose: Adjusts raw p-values for false discovery rate.
  - Method: `double[][] adjustPValues(double[][] rawPValues);`

## Step 2: Math Library Abstraction (Adapter Pattern)
Abstract the math library to prevent vendor lock-in (e.g., Apache Commons Math) and allow future migrations to high-performance libraries. All core domain logic will rely strictly on primitive arrays (`double[][]`).

- **`ILinearAlgebra` (Interface)**
  - Path: `mbc/src/main/java/Interfaces/ILinearAlgebra.java`
  - Purpose: Defines matrix operations using only primitive arrays.
  - Methods: `multiply`, `add`, `invert`, `getDiagonal`, `createScaledIdentity`.

- **`IProbabilityProvider` (Interface)**
  - Path: `mbc/src/main/java/Interfaces/IProbabilityProvider.java`
  - Purpose: Provides statistical distribution calculations.
  - Method: `double calculateTwoTailedPValue(double zScore);`

- **`CommonsMathLinearAlgebra` (Adapter Class)**
  - Path: `mbc/src/main/java/MathAdapters/CommonsMathLinearAlgebra.java` (New Package: `MathAdapters`)
  - Purpose: Implements `ILinearAlgebra` using `org.apache.commons.math3.linear.Array2DRowRealMatrix` and `LUDecomposition`.

- **`CommonsMathProbability` (Adapter Class)**
  - Path: `mbc/src/main/java/MathAdapters/CommonsMathProbability.java`
  - Purpose: Implements `IProbabilityProvider` using `org.apache.commons.math3.distribution.NormalDistribution`.

## Step 3: Implement the GLM Processor
Extract the GLM logic currently trapped inside `IRLS.java`.

- **`GLMProcessor` (Class)**
  - Path: `mbc/src/main/java/ModelFitters/GLMProcessor.java` (New Package: `ModelFitters`)
  - Purpose: Implements `IModelFitter`. Duplicates the mathematical core of `IRLS.java` (mean/variance compression, iteratively reweighted least squares loops, Gaussian elimination).
  - Difference: Instead of returning just `betas` (`double[][]`), it returns a fully populated `GLMFitResult`.

## Step 4: Implement Significance Testing and FDR
Implement the mathematical logic using our new abstractions.

- **`WaldTestSignificanceTester` (Class)**
  - Path: `mbc/src/main/java/Significance/WaldTestSignificanceTester.java`
  - Logic: Injects `ILinearAlgebra` and `IProbabilityProvider`. Uses the abstract methods to invert $(X^T W X + \lambda I)$ and compute standard errors and p-values, ensuring zero direct dependency on Apache Commons Math.

- **`BenjaminiHochbergCorrection` (Class)**
  - Path: `mbc/src/main/java/Significance/BenjaminiHochbergCorrection.java`
  - Logic: Flattens p-values, sorts them, applies $P_{adj} = \min(P_{(k)} \cdot \frac{m \cdot T}{k}, 1)$, enforces monotonicity, and remaps to original dimensions.

## Step 5: Create the TracGLMWorkflow Orchestrator
Build the new workflow to replace the standard one for GLM-based analysis.

- **`TracGLMWorkflow` (Class)**
  - Path: `mbc/src/main/java/ClusterWorkflow/TracGLMWorkflow.java`
  - Implements: `ClusterWorkflow`
  - Flow:
    1. Base Normalization (e.g., MedianRatiosNormalization).
    2. GLM Fitting via `GLMProcessor`.
    3. Significance Testing via `WaldTestSignificanceTester` (using Math Adapters).
    4. FDR Correction via `BenjaminiHochbergCorrection`.
    5. Significance Filtering: Retain $\beta$ trajectories with at least one $P_{adj} < 0.05$.
    6. Clustering via the provided `IClusteringAlgorithm`.

## Step 6: Integration in Main Services
Update the entry points to use the new workflow gracefully.

- **`ClusterBenchmarkService` / `ClusterGenerationService`**
  - Logic: Check if the user intends to use the GLM approach. If so, instantiate and use `TracGLMWorkflow` instead of `StandardClusterWorkflow`. Otherwise, fallback to the existing `StandardClusterWorkflow` behavior.
