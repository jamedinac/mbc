package Filter;

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
    public boolean filterGene(double[] geneExpressionRow) {
        boolean result = true;

        for (IGeneFilter filter : this.filters) {
            result &= filter.filterGene(geneExpressionRow);
        }

        return result;
    }
}
