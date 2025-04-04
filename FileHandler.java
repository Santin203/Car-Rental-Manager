import java.io.*;
import java.util.List;

public class FileHandler {
    public static void saveToFile(String filename, List<ICar> cars) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            for (ICar car : cars) {
                writer.write(car.getPlate() + "," + car.getType() + "," + car.getMileage());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFromFile(String filename, ICarLot carLot) {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                ICar car = new Car(parts[0], parts[1]);
                car.updateMileage(Integer.parseInt(parts[2]));
                carLot.addCar(car);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

