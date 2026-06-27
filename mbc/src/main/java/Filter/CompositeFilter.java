package Filter;

import Enum.FilterStatus;
import Interfaces.IGeneFilter;

import java.util.ArrayList;

public class CompositeFilter implements IGeneFilter {

    private final ArrayList<IGeneFilter> filters;

    public CompositeFilter() {
        this.filters = new ArrayList<>();
    }

    public void addfilter(IGeneFilter filter) {
        this.filters.add(filter);
    }

    @Override
    public FilterStatus filterGene(double[] geneExpressionRow) {
        for (IGeneFilter filter : this.filters) {
            FilterStatus status = filter.filterGene(geneExpressionRow);
            if (status != FilterStatus.NOT_FILTERED) {
                return status;
            }
        }

        return FilterStatus.NOT_FILTERED;
    }
}
