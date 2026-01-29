package DataGenerators;

import Interfaces.IDataGenerator;
import java.util.Random;

public class UniformDataGenerator implements IDataGenerator {

    private final Random random;
    private final double limit;

    public UniformDataGenerator(double limit) {
        this.random = new Random();
        this.limit = limit;
    }

    public double generateRandomDouble() {return random.nextDouble(limit);}
}
