import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileHandler {
    private static final Set<String> writtenPlates = new HashSet<>(); //

    /*
     * Prompt: Create a method to save the cars to a file. 
     * The method should take the filename, the list of cars, and 
     * a boolean indicating whether to append or overwrite as parameters.
     */
    public static synchronized void saveCarsToFile(String filename, List<ICar> cars, boolean append) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, append))) {
            for (ICar car : cars) {
                writer.write(car.getPlate() + "," + car.getType() + "," + car.getMileage());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Prompt: Create a method to load the cars from a file. 
     * The method should take the filename and a car lot as parameters.
     * It should create the file if it does not exist
     */
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

    public static void saveLicensePlates(String filename, List<ICar> cars, String lotFilename) {
        File file = new File(filename);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    writtenPlates.add(parts[0]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            for (ICar car : cars) {
                if (!writtenPlates.contains(car.getPlate())) {
                    writer.write(car.getPlate() + "," + lotFilename);
                    writer.newLine();
                    writtenPlates.add(car.getPlate()); //
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Prompt: Create a method to remove a license plate from the licensePlates.txt file.
     * The method should take the filename and the license plate as parameters.
     */
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

    public static synchronized void saveShopCarsToFile(String location, List<ICar> cars) {
        String filename = location + "_shop.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (ICar car : cars) {
                writer.write(car.getPlate() + "," + car.getType() + "," + car.getMileage());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ICar> getCarsFromFile(String filename) {
        List<ICar> cars = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                ICar car = new Car(parts[0], parts[1]);
                car.updateMileage(Integer.parseInt(parts[2])); //
                cars.add(car);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cars;
    }

    public static List<String> readLinesFromFile(String filePath) {
        try {
            return Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            e.printStackTrace();
            return List.of();
        }
    }

    /*
     * Prompt: Create a method to get the discount applied status for a license plate from the rented_cars.txt file.
     */
    public static Boolean getDiscountApplied(String licensePlate) {
        for (String line : FileHandler.readLinesFromFile("rented_cars.txt")) {
            String[] parts = line.split(",");
            if (parts.length > 3 && parts[0].equals(licensePlate)) {
                return Boolean.parseBoolean(parts[3]);
            }
        }
        return false;
    }

    public static void saveRentedCarsToFileAppend(String filename, List<ICar> cars, boolean discountApplied) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            for (ICar car : cars) {
                writer.write(car.getPlate() + "," + car.getType() + "," + car.getMileage() + "," + discountApplied);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void removeRentedCarFromFile(String filename, ICar car) {
        File file = new File(filename);
        File tempFile = new File("temp_" + filename);

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (!parts[0].equals(car.getPlate())) {
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

    /*
     * Prompt: Make a method to update the location of a license plate in the licensePlates.txt file.
     * The method should take the filename, the license plate, and the new location as parameters.
     */
    public static void updateLicensePlateLocation(String filename, String plate, String newLocation) {
        File file = new File(filename);
        File tempFile = new File("temp_" + filename);
    
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
    
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(plate)) {
                    // Update the location
                    writer.write(parts[0] + "," + newLocation);
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // Replace the original file with the updated file
        if (!file.delete() || !tempFile.renameTo(file)) {
            System.err.println("Failed to update " + filename);
        }
    }

    public static void saveShopConfig(String location, int maxSpaces, List<String> allowedLots) {
        String filename = location + "_config.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(String.valueOf(maxSpaces));
            writer.newLine();
            if (allowedLots != null && !allowedLots.isEmpty()) {
                writer.write(String.join(",", allowedLots));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a shop configuration file exists.
     * @param location The shop location
     * @return true if the configuration file exists, false otherwise
     */
    public static boolean shopConfigExists(String location) {
        String filename = location + "_config.txt";
        return new File(filename).exists();
    }

    public static List<Object> loadShopConfig(String location) {
        String filename = location + "_config.txt";
        File file = new File(filename);
        int maxSpaces = 10; // Default value
        List<String> allowedLots = new ArrayList<>();
        
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String spacesLine = reader.readLine();
                if (spacesLine != null && !spacesLine.isEmpty()) {
                    maxSpaces = Integer.parseInt(spacesLine);
                }
                
                String lotsLine = reader.readLine();
                if (lotsLine != null && !lotsLine.isEmpty()) {
                    String[] lots = lotsLine.split(",");
                    allowedLots.addAll(Arrays.asList(lots));
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        return Arrays.asList(maxSpaces, allowedLots);
    }

    /*
     * Prompt: Create a method to get the transaction summary from the transactions file.
     * The method should return a list containing the total revenue and total discounts.
     * It is saved in the first line of the file.
     */
    public static List<Object> getTransactionSummary(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Transactions file does not exist: " + filename);
            return List.of(0.0, 0.0); // Return default summary if file doesn't exist
        }
    
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Read the first line (summary line)
            if (line != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    double totalRevenue = Double.parseDouble(parts[0]);
                    double totalDiscounts = Double.parseDouble(parts[1]);
                    return List.of(totalRevenue, totalDiscounts);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    
        return List.of(0.0, 0.0); // Return default summary in case of an error
    }

    /*
     * Prompt: Create a method to save a transaction to the transactions file.
     * The method should take the filename and the transaction as parameters.
     * It should skip the first line (summary line) and append the transaction to the file.
     */
    public static List<ITransaction> getTransactionsFromFile(String filename) {
        List<ITransaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip the summary line
                    continue;
                }
                String[] parts = line.split(",");
                ICar car = new Car(parts[0], parts[1]);
                ITransaction transaction = new Transaction(Double.parseDouble(parts[2]), Boolean.parseBoolean(parts[3]), car, Double.parseDouble(parts[4]));
                transactions.add(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    /*
     * Prompt: Create a method to save a transaction to the transactions file.
     * The method should take the filename and the transaction as parameters.
     * It should skip the first line (summary line) and append the transaction to the file.
     */
    public static void saveTransactionToFile(String filename, ITransaction transaction) {
        File file = new File(filename);
        List<ITransaction> transactions = new ArrayList<>();
    
        // Load existing transactions if the file exists
        if (file.exists()) {
            transactions = getTransactionsFromFile(filename);
        }
    
        // Add the new transaction to the list
        transactions.add(transaction);
    
        // Write the summary line and all transactions to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) {
            double totalRevenue = transactions.stream().mapToDouble(ITransaction::getAmount).sum();
            double totalDiscounts = transactions.stream()
                                                .filter(ITransaction::isDiscountApplied)
                                                .mapToDouble(ITransaction::getDiscountedAmount)
                                                .sum();
    
            // Write the summary line
            writer.write(totalRevenue + "," + totalDiscounts);
            writer.newLine();
    
            // Write each transaction
            for (ITransaction t : transactions) {
                writer.write(t.getCar().getPlate() + "," + t.getCar().getType() + "," + t.getAmount() + "," + t.isDiscountApplied() + "," + t.getDiscountedAmount());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

