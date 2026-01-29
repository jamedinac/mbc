package Interfaces;

public interface IGeneProfile<T extends Number> {
    /**
     * Gets the maximum expression value from the gene profile
     * @return maximum value from the gene profile
     */
    public T getMaxExpression();

    /**
     * Gets the expression value at index i
     * @return Expression level at the i
     * @param i index to return
     */
    public T getIndex(int i);

    /**
     * Gets the number of components in the gene expression profile
     * @return the number of components in the gene expression
     */
    public Integer getNumberOfComponents();
}
