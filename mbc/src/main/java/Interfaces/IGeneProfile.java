package Interfaces;

import java.util.ArrayList;

public interface IGeneProfile<T extends Number> {

    /**
     * Gets the Array List containing the gene expression data
     * @return Array list with the gene expression data
     */
    public ArrayList<T> getProfileExpression();

    /**
     * Defines an Add operation for two gene profile expression
     * @param profile gene profile to be added
     * @return the Gene profile result of the Add operation
     */
    public IGeneProfile add(IGeneProfile profile);

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
