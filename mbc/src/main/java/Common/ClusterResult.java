package Common;

public class ClusterResult {

    /*
    * TODO:
    *   Tells how good is the clustering based on metrics,
    *   it can add the gold standard
    *
    *   Add metrics: jackard, mean square,...
    *   Default value for those ones that don't have gold standard
    *   add gold standard as other gene clustering data parameter
    */
    private boolean good;

    public ClusterResult(boolean good) {
        this.good = good;
    }

}
