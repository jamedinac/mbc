package GeneProfile;

import java.util.ArrayList;

public abstract class AGeneProfile <T extends Number> {

    protected String geneId;
    protected ArrayList<T> profileExpression;

    /**
     * Defines an Add operation for two gene profile expression
     * @param profile gene profile to be added
     * @return the Gene profile result of the Add operation
     */
    public abstract AGeneProfile<T> add(AGeneProfile<T> profile);

    /**
     * Gets the maximum expression value from the gene profile
     * @return maximum value from the gene profile
     */
    public abstract T getMaxExpression();

    /**
     * Gets the Array List containing the gene expression data
     * @return Array list with the gene expression data
     */
    public ArrayList<T> getProfileExpression() {
        return this.profileExpression;
    }

    /**
     * Gets the expression value at index i
     * @return Expression level at the i
     * @param i index to return
     */
    public T getIndex(int i) {
        return this.profileExpression.get(i);
    }

    /**
     * Gets the number of components in the gene expression profile
     * @return the number of components in the gene expression
     */
    public Integer getNumberOfComponents() {
        return this.profileExpression.size();
    }

    public String getGeneId() {
        return this.geneId;
    }
}
