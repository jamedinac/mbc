package Filter;

import Interfaces.IGeneFilter;
import java.util.List;

public class FilterFactory {
    public static IGeneFilter createFilter(String filterType, List<String> params) {
        return switch (filterType.toLowerCase()) {
            case "non-zero" -> new ZeroFilter();
            case "variance" -> new GeneFilterByVariance(Double.parseDouble(params.get(0)));
            case "total-expression" -> new GeneFilterByTotalExpression(Double.parseDouble(params.get(0)));
            default -> throw new IllegalArgumentException("Unknown filter type: " + filterType);
        };
    }

    public static CompositeFilter createCompositeFilter(List<String> filterArgs) {
        CompositeFilter compositeFilter = new CompositeFilter();
        
        // Apply default filters if no filters are provided
        if (filterArgs == null || filterArgs.isEmpty()) {
            compositeFilter.addfilter(new ZeroFilter());
            compositeFilter.addfilter(new GeneFilterByVariance(1));
            return compositeFilter;
        }

        for (int i = 0; i < filterArgs.size(); i++) {
            String filterType = filterArgs.get(i).toLowerCase();
            switch (filterType) {
                case "non-zero" -> compositeFilter.addfilter(new ZeroFilter());
                case "variance", "total-expression" -> {
                    if (i + 1 < filterArgs.size()) {
                        String param = filterArgs.get(++i);
                        compositeFilter.addfilter(createFilter(filterType, List.of(param)));
                    }
                }
            }
        }
        return compositeFilter;
    }
}
