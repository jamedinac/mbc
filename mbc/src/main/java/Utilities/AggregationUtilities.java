package Utilities;

import java.util.ArrayList;

/**
 * Utility class providing mathematical aggregation functions.
 */
public class AggregationUtilities {
    
    /**
     * Calculates the unbiased sample variance of an array list of doubles.
     *
     * @param a the list of numerical values
     * @return the variance of the values, or 0.0 for fewer than two values
     */
    public static double variance(ArrayList<Double> a) {
        int n = a.size();
        if (n < 2) {
            return 0.0;
        }

        double res = 0.0;
        double m = mean(a);

        for (int i=0; i<n; ++i) {
            res += (a.get(i) - m) * (a.get(i) - m);
        }

        return res / (n - 1);
    }

    /**
     * Calculates the arithmetic mean of an array list of doubles.
     * 
     * @param a the list of numerical values
     * @return the arithmetic mean
     */
    public static double mean(ArrayList<Double> a) {
        int n =  a.size();
        double res = 0.0;

        for (int i = 0; i < n; ++i) {
            res += a.get(i);
        }

        return res / n;
    }

    /**
     * Finds the maximum absolute element in a double array.
     * 
     * @param a the array of numerical values
     * @return the maximum absolute value
     */
    public static double maxAbsElement(double[] a) {
        double res = Math.abs(a[0]);
        for (int i = 1; i < a.length; i++) {
            res = Math.max(res, Math.abs(a[i]));
        }
        return res;
    }
}
