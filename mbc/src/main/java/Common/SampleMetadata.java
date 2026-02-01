package Common;

public class SampleMetadata {

    private final String id;

    private final int replicate;

    private final int time;

    public SampleMetadata(String id, int replicate, int time) {
        this.id = id;
        this.replicate = replicate;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public int getReplicate() {
        return replicate;
    }

    public int getTime() {
        return time;
    }
}
