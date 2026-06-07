package Interfaces;

/**
 * Abstraction layer for linear algebra operations used within the statistical models.
 * Relying on this interface prevents vendor lock-in to specific math libraries 
 * (like Apache Commons Math) and allows easy swapping to GPU-accelerated or 
 * highly optimized native BLAS libraries in the future. All methods operate on standard Java primitives.
 */
public interface ILinearAlgebra {
    
    /**
     * Multiplies two matrices (A * B).
     *
     * @param a The first matrix.
     * @param b The second matrix.
     * @return The resulting product matrix.
     */
    double[][] multiply(double[][] a, double[][] b);

    /**
     * Adds two matrices (A + B).
     *
     * @param a The first matrix.
     * @param b The second matrix.
     * @return The resulting sum matrix.
     */
    double[][] add(double[][] a, double[][] b);

    /**
     * Computes the inverse of a square matrix.
     *
     * @param matrix The square matrix to invert.
     * @return The inverted matrix.
     */
    double[][] invert(double[][] matrix);

    /**
     * Extracts the main diagonal elements of a given matrix.
     *
     * @param matrix The matrix from which to extract the diagonal.
     * @return A 1D array containing the diagonal elements.
     */
    double[] getDiagonal(double[][] matrix);

    /**
     * Creates an identity matrix of the specified size and scales it by the given factor.
     * Useful for constructing Ridge regularization penalty matrices (lambda * I).
     *
     * @param size  The dimension of the square identity matrix.
     * @param scale The factor to multiply the identity matrix by (e.g., lambda).
     * @return A scaled identity matrix.
     */
    double[][] createScaledIdentity(int size, double scale);
}
