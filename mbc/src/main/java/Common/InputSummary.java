package Common;

/**
 * Lightweight summary of the raw input data dimensions,
 * captured before filtering to avoid retaining the full expression matrix in memory.
 */
public class InputSummary {

    private final int geneCount;
    private final int sampleCount;

    public InputSummary(int geneCount, int sampleCount) {
        this.geneCount = geneCount;
        this.sampleCount = sampleCount;
    }

    public int getGeneCount() {
        return geneCount;
    }

    public int getSampleCount() {
        return sampleCount;
    }
}
