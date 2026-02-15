package Common;

public class SampleTrait {
    private String trait;
    private String value;

    public SampleTrait(String trait, String value) {
        this.trait = trait;
        this.value = value;
    }

    public String getTrait() {
        return trait;
    }

    public String getValue() {
        return this.value;
    }
}
