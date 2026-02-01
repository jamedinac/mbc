package DataGenerators;

import Interfaces.IDataGenerator;
import java.util.Random;

public class UniformDataGenerator implements IDataGenerator {

    private final Random random;

    public UniformDataGenerator() {
        this.random = new Random();
    }

    public double generateRandomDouble(double limit) {return random.nextDouble(limit);}

    public int generateRandomInt(int limit) {return random.nextInt(limit);}
}
