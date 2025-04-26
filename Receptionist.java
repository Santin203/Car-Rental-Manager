import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Receptionist {
    /*
     * Prompt: Generate an static method to process the command line arguments.
     * Depending on the arguments, it should either initialize a RentalShop or a LotManager.
     * For example, if the arguments contain "--lot-name", it should initialize a LotManager.
     * if the arguments contain "--location", it should initialize a RentalShop.
     */
    public static void processCommands(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java Main [--lot-name | --location] ...");
            return;
        }

        if (Arrays.stream(args).anyMatch(arg -> arg.startsWith("--lot-name="))) {
            processLotManagerArgs(args);
        } else if (Arrays.stream(args).anyMatch(arg -> arg.startsWith("--location="))) {
            processShopArgs(args);
        } else {
            System.out.println("Error: Missing required flags (--lot-name or --location)");
        }
    }

    /*
     * Prompt: Generate a static method to process the command line arguments for LotManager.
     * It should handle adding and removing cars from the lot based on the arguments provided.
     * Provide the skeleton for later modifications
     */
    private static void processLotManagerArgs(String[] args) {
        String lotName = null;
        ILotManager manager = null;

        for (String arg : args) {
            if (arg.startsWith("--lot-name=")) {
                lotName = arg.split("=")[1];
                manager = new LotManager(lotName);
            }
        }

        if (manager == null) {
            System.out.println("Error: --lot-name flag is required.");
            return;
        }

        for (String arg : args) {
            if (arg.startsWith("--add-sedan=")) {
                int count = Integer.parseInt(arg.split("=")[1]);
                manager.addCars("Sedan", count);
            } else if (arg.startsWith("--add-suv=")) {
                int count = Integer.parseInt(arg.split("=")[1]);
                manager.addCars("SUV", count);
            } else if (arg.startsWith("--add-van=")) {
                int count = Integer.parseInt(arg.split("=")[1]);
                manager.addCars("Van", count);
            } else if (arg.startsWith("--remove-vehicle=")) {
                String plate = arg.split("=")[1];
                manager.removeCar(plate);
            }
        }
    }

    /*
     * Prompt: Generate a static method to process the command line arguments for RentalShop.
     * It should handle adding and removing cars from the lot based on the arguments provided.
     * Provide the skeleton for later modifications
     */
    private static void processShopArgs(String[] args) {
        String location = null;
        int spaces = 10;
        List<String> lots = List.of();

        for (String arg : args) {
            if (arg.startsWith("--location=")) {
                location = arg.split("=")[1];
            } else if (arg.startsWith("--spaces-available=")) {
                spaces = Integer.parseInt(arg.split("=")[1]);
            } else if (arg.startsWith("--lots=")) {
                String[] lotNames = arg.split("=")[1].split(",");
                lots = Arrays.asList(lotNames);
            }
        }

        if (location == null) {
            System.out.println("Error: --location is required for rental shop initialization.");
            return;
        }

        // Check if a shop configuration file exists for this location
        if (FileHandler.shopConfigExists(location)) {
            // If configuration exists, load it and ignore command line values
            List<Object> config = FileHandler.loadShopConfig(location);
            spaces = (int) config.get(0);
            lots = (List<String>) config.get(1);
            System.out.println("Loading existing shop configuration for " + location + ":");
            System.out.println("- Spaces available: " + spaces);
            System.out.println("- Allowed lots: " + String.join(", ", lots));
        } else {
            // If no configuration exists, create one with the command line values
            FileHandler.saveShopConfig(location, spaces, lots);
            System.out.println("Created new shop configuration for " + location);
        }

        RentalShop shop = new RentalShop(location, spaces);
        ShopManager manager = new ShopManager(shop, lots);
        manager.loadShop();

        System.out.println("Shop initialized at " + location);
        manager.runLoop();
    }

    public static void getCurrentLots() {
        File folder = new File("."); //
        File[] lotFiles = folder.listFiles((dir, name) -> name.endsWith("_lot.txt")); //

        if (lotFiles != null) {
            for (File lotFile : lotFiles) {
                loadCarsFromLotFile(lotFile.getName());
            }
        }
    }

    public static void loadCarsFromLotFile(String lotFilename) {
        ICarLot carLot = new CarLot(lotFilename);
        FileHandler.loadFromFile(lotFilename, carLot);
        FileHandler.saveLicensePlates("licensePlates.txt", carLot.getCars(), lotFilename);
    }
}
