package Filter;

import Common.SampleMetadata;
import Interfaces.ISampleFilter;

import java.util.HashMap;
import java.util.HashSet;

public class SampleFilter implements ISampleFilter {

    private final HashMap<String, HashSet<String>> validSampleTrait;

    public SampleFilter() {
        this.validSampleTrait = new HashMap<>();
    }

    @Override
    public void addValidSampleTrait(String sampleTrait, String validSampleTrait) {
        if (!this.validSampleTrait.containsKey(sampleTrait)) {
            this.validSampleTrait.put(sampleTrait, new HashSet<>());
        };

        var sampleTraitValues = this.validSampleTrait.get(sampleTrait);
        sampleTraitValues.add(validSampleTrait);

        this.validSampleTrait.put(sampleTrait, sampleTraitValues);
    }

    @Override
    public boolean isValidSample(SampleMetadata sampleMetadata) {
        if (sampleMetadata == null) {
            return false;
        }

        boolean isValid = true;
        for (HashMap.Entry<String, String> sampleEntry : sampleMetadata.getMetadata().entrySet()) {
            String sampleTrait = sampleEntry.getKey();
            String sampleTraitValue = sampleEntry.getValue();

            isValid &= !this.validSampleTrait.containsKey(sampleTrait) || this.validSampleTrait.get(sampleTrait).contains(sampleTraitValue);
        }
        return isValid;
    }
}
