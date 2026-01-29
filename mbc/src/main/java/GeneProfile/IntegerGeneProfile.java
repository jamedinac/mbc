package GeneProfile;

import java.util.ArrayList;
import java.util.Collections;

public class IntegerGeneProfile extends AGeneProfile<Integer> {

    public IntegerGeneProfile(ArrayList<Integer> profileExpression, String geneId) {
        this.profileExpression = profileExpression;
        this.geneId = geneId;
    }

    /**
     * Defines an Add operation for two gene profile expression
     * @param profile gene profile to be added
     * @return the Gene profile result of the Add operation
     */
    public AGeneProfile<Integer> add(AGeneProfile<Integer> profile){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gets the maximum expression value from the gene profile
     * @return maximum value from the gene profile
     */
    public Integer getMaxExpression() {
        return Collections.max(this.profileExpression);
    }

    /**
     * Parses to DoubleGeneProfile
     * @return DoubleGeneProfile
     */
    public DoubleGeneProfile parseToDouble() {
        ArrayList<Double> doubleExpressionData = new ArrayList<>();

        for (Integer gene : this.profileExpression) {
            doubleExpressionData.add(gene.doubleValue());
        }

        return  new DoubleGeneProfile(doubleExpressionData, this.getGeneId());
    }
}
