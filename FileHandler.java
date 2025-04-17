import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileHandler {
    private static final Set<String> writtenPlates = new HashSet<>();

    public static void saveCarsToFile(String filename, List<ICar> cars) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (ICar car : cars) {
                writer.write(car.getPlate() + "," + car.getType() + "," + car.getMileage());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveCarsToFileAppend(String filename, List<ICar> cars) {
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

    public static void saveLicensePlatesAppend(String filename, List<ICar> cars) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            for (ICar car : cars) {
                writer.write(car.getPlate() + "," + false);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveLicensePlates(String filename, List<ICar> cars, String lotFilename) {
        // Load existing license plates from the file
        File file = new File(filename);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    writtenPlates.add(parts[0]); // Add existing plates to the set
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Append only new license plates to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            for (ICar car : cars) {
                if (!writtenPlates.contains(car.getPlate())) { // Avoid duplicates
                    writer.write(car.getPlate() + "," + false + "," + lotFilename);
                    writer.newLine();
                    writtenPlates.add(car.getPlate()); // Mark as written
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeLicensePlate(String filename, String plate) {
        File file = new File(filename);
        File tempFile = new File("temp_" + filename);

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (!parts[0].equals(plate) || !parts[1].equals("false")) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Replace the original file with the updated file
        if (!file.delete() || !tempFile.renameTo(file)) {
            System.err.println("Failed to update " + filename);
        }
    }
}

