/*
 * Prompt: Create a class named LicensePlateGenerator that generates 
 * unique and random license plates. They should have the following format:
 * "ABC-123", where "ABC" are random uppercase letters and "123" are random digits.
 */
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LicensePlateGenerator {
    private static Set<String> usedPlates = new HashSet<>();
    private static final Random RANDOM = new Random();

    public static String generate() {
        String plate;
        do {
            char first = (char) ('A' + RANDOM.nextInt(26));
            char second = (char) ('A' + RANDOM.nextInt(26));
            char third = (char) ('A' + RANDOM.nextInt(26));
            int number = RANDOM.nextInt(1000);
            plate = String.format("%c%c%c-%03d", first, second, third, number);
        } while (usedPlates.contains(plate));

        usedPlates.add(plate);
        return plate;
    }
}

