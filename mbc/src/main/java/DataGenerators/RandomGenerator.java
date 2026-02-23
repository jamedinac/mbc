package DataGenerators;

import java.util.Random;

public class RandomGenerator {

    private static final Random random = new Random();

    public static int uniformRandomIntInRange(int min, int max) {
        return uniformRandomInt(max - min + 1) + min;
    }

    public static int uniformRandomInt(int max) {
        return random.nextInt(max);
    }

    public static double uniformRandomDoubleInRange(double a, double b) {
        return uniformRandomDouble(b - a) + a;
    }

    public static double uniformRandomDouble(double limit) {
        return random.nextDouble(limit);
    }

    public static double gaussianRandom(double mean, double sd) {
        return random.nextGaussian() * sd + mean;
    }

    public static boolean randomBoolean() {
        return random.nextBoolean();
    }
}
