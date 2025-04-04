public class Receptionist {
    /*
     * Generate an static method to process the command line arguments and perform the actions
     */
    public static void processCommands(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java Main --lot-name=<name> [commands]");
            return;
        }

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
}

