package DataGenerators;

import Interfaces.IDataGenerator;
import java.util.Random;

public class UniformDataGenerator implements IDataGenerator {

    private final Random random;
    private final int limit;

    public UniformDataGenerator(int limit) {
        this.random = new Random();
        this.limit = limit;
    }

    public int generateRandomNumber() {
        return random.nextInt(limit);
    }
}
