package GeneFilter;

import Interfaces.IGeneFilter;

import java.util.ArrayList;

public class CompositeFilter implements IGeneFilter {

    private ArrayList<IGeneFilter> filters;

    public void addfilter(IGeneFilter filter) {
        filters.add(filter);
    }

    @Override
    public boolean filterGene(String geneExpressionRow) {
        boolean result = true;

        for (IGeneFilter filter : filters) {
            result &= filter.filterGene(geneExpressionRow);
        }

        return result;
    }
}
