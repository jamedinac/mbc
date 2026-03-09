package Interfaces;

import Common.SampleMetadata;

/**
 * Interface defining a condition to filter samples based on their metadata traits.
 */
public interface ISampleFilter {

    /**
     * Registers a valid trait value that a sample must match to be included.
     * 
     * @param sampleTrait the trait category (e.g., 'Condition', 'Tissue')
     * @param validSampleTrait the valid value for the trait (e.g., 'Treatment', 'Liver')
     */
    void addValidSampleTrait(String sampleTrait, String validSampleTrait);

    /**
     * Evaluates if a given sample's metadata meets all registered valid traits.
     * 
     * @param sampleMetadata the metadata properties of the sample
     * @return true if the sample meets the criteria and should be kept, false otherwise
     */
    boolean isValidSample(SampleMetadata sampleMetadata);
}
