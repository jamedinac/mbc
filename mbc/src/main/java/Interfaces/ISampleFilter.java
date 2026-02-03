package Interfaces;

import Common.SampleMetadata;

public interface ISampleFilter {

    void addValidSampleTrait(String sampleTrait, String validSampleTrait);

    boolean isValidSample(SampleMetadata sampleMetadata);
}
