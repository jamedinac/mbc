package Common;

import java.util.HashMap;

public class SampleMetadata {

    private final String id;

    private final int replicate;

    private final int time;

    private final HashMap<String, String> metadata;

    public SampleMetadata(String id, int replicate, int time) {
        this.id = id;
        this.replicate = replicate;
        this.time = time;
        metadata = new HashMap<>();
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

    public void addMetadataKey(String key, String value) {
        metadata.put(key, value);
    }

    public String getMetadataParameter(String key) {
        return metadata.get(key);
    }

    public HashMap<String, String> getMetadata() {
        return metadata;
    }
}
