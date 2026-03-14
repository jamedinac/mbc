package Filter;

import Interfaces.ISampleFilter;
import java.util.List;

public class SampleFilterFactory {

    /**
     * Creates a SampleFilter based on a list of trait-value pairs.
     * 
     * @param filterArgs List of arguments in the format [trait1, value1, trait2, value2, ...]
     * @return a configured ISampleFilter
     */
    public static ISampleFilter createSampleFilter(List<String> filterArgs) {
        SampleFilter sampleFilter = new SampleFilter();
        
        if (filterArgs != null) {
            for (int i = 0; i < filterArgs.size(); i += 2) {
                if (i + 1 < filterArgs.size()) {
                    String trait = filterArgs.get(i);
                    String value = filterArgs.get(i + 1);
                    sampleFilter.addValidSampleTrait(trait, value);
                }
            }
        }
        
        return sampleFilter;
    }
}
