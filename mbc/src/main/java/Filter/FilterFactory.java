package Filter;

import Interfaces.IGeneFilter;
import java.util.List;

/**
 * Factory for creating and organizing gene filters into pre-normalization and post-normalization stages.
 */
public class FilterFactory {

    /**
     * Record to hold the partitioned filter groups.
     *
     * @param preNormalization  Filters applied to raw/normalized expression data.
     * @param postNormalization Filters applied to statistical outputs (e.g., p-values).
     */
    public record FilterSetup(IGeneFilter preNormalization, IGeneFilter postNormalization) {}

    /**
     * Creates a single gene filter instance based on type and parameters.
     */
    public static IGeneFilter createFilter(String filterType, List<String> params) {
        return switch (filterType.toLowerCase()) {
            case "non-zero" -> new ZeroFilter();
            case "variance" -> new GeneFilterByVariance(Double.parseDouble(params.get(0)));
            case "total-expression" -> new GeneFilterByTotalExpression(Double.parseDouble(params.get(0)));
            case "significance" -> new SignificanceFilter(Double.parseDouble(params.get(0)));
            default -> throw new IllegalArgumentException("Unknown filter type: " + filterType);
        };
    }

    /**
     * Parses the CLI filter arguments and partitions them into pre and post normalization stages.
     *
     * @param filterArgs List of filter names and their parameters from the CLI.
     * @return A FilterSetup containing the partitioned composite filters.
     */
    public static FilterSetup createFilterSetup(List<String> filterArgs) {
        CompositeFilter pre = new CompositeFilter();
        CompositeFilter post = new CompositeFilter();

        // Apply default pre-normalization filters if no filters are provided
        if (filterArgs == null || filterArgs.isEmpty()) {
            pre.addfilter(new ZeroFilter());
            pre.addfilter(new GeneFilterByVariance(1.0));
            return new FilterSetup(pre, post);
        }

        for (int i = 0; i < filterArgs.size(); i++) {
            String filterType = filterArgs.get(i).toLowerCase();
            switch (filterType) {
                case "non-zero" -> pre.addfilter(new ZeroFilter());
                case "variance", "total-expression" -> {
                    if (i + 1 < filterArgs.size()) {
                        String param = filterArgs.get(++i);
                        pre.addfilter(createFilter(filterType, List.of(param)));
                    }
                }
                case "significance" -> {
                    if (i + 1 < filterArgs.size()) {
                        String param = filterArgs.get(++i);
                        post.addfilter(new SignificanceFilter(Double.parseDouble(param)));
                    }
                }
            }
        }
        return new FilterSetup(pre, post);
    }
}
