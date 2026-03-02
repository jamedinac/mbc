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

        for (HashMap.Entry<String, HashSet<String>> filterEntry : this.validSampleTrait.entrySet()) {
            String requiredTrait = filterEntry.getKey();
            String sampleValue = sampleMetadata.getMetadataParameter(requiredTrait);

            if (sampleValue == null || !filterEntry.getValue().contains(sampleValue)) {
                return false;
            }
        }
        return true;
    }
}
