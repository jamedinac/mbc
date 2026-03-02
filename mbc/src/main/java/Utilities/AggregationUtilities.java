package Utilities;

import java.util.ArrayList;

public class AggregationUtilities {
    public static double variance(ArrayList<Double> a) {
        int n = a.size();
        double res = 0.0;
        double m = mean(a);

        for (int i=0; i<n; ++i) {
            res += (a.get(i) - m) * (a.get(i) - m);
        }

        return res / n;
    }
    public static double mean(ArrayList<Double> a) {
        int n =  a.size();
        double res = 0.0;

        for (int i = 0; i < n; ++i) {
            res += a.get(i);
        }

        return res / n;
    }

    public static double maxElement(double[] a) {
        double res = a[0];
        for (int i=0; i<a.length; i++) {
            res = Math.max(res, a[i]);
        }
        return res;
    }

    public static double maxAbsElement(double[] a) {
        double res = Math.abs(a[0]);
        for (int i = 1; i < a.length; i++) {
            res = Math.max(res, Math.abs(a[i]));
        }
        return res;
    }
}
