import java.util.List;

public class ShopManager implements IShopManager {
    private RentalShop shop;
    private List<String> allowedLots;

    public ShopManager(RentalShop shop, List<String> allowedLots) {
        this.shop = shop;
        this.allowedLots = allowedLots;
    }

    /*
     * Prompt: Load into the program the cars already in the shop file. 
     * If there are no cars in the shop, load them from the allowed lots.
     */
    public void loadShop() {
        // Load into the program the cars already in the shop file
        List<ICar> shopCars = FileHandler.getCarsFromFile(shop.getLocation() + "_shop.txt");

        if (shopCars != null) {
            java.util.Iterator<ICar> iterator = shopCars.iterator();
            while (iterator.hasNext()) {
                ICar car = iterator.next();
                if (shop.canFitAnotherCar()) {
                    shop.addCar(car);
                    FileHandler.updateLicensePlateLocation("licensePlates.txt", car.getPlate(), shop.getLocation());
                } else {

                    String lotName = null;
                    if (!allowedLots.isEmpty()) {
                        lotName = allowedLots.get(0);
                    } else {
                        System.out.println("No allowed lots available");
                        return;
                    }

                    ICarLot carLot = new CarLot(lotName);
                    FileHandler.loadFromFile(lotName + ".txt", carLot);
                    carLot.addCar(car);
                    FileHandler.saveCarsToFile(lotName + ".txt", carLot.getCars(), false);
                    FileHandler.updateLicensePlateLocation("licensePlates.txt", car.getPlate(), lotName);
                }
                iterator.remove();
            }
        }

        for (String lotName : allowedLots) {
            ICarLot carLot = new CarLot(lotName);
            FileHandler.loadFromFile(lotName + ".txt", carLot);

            java.util.Iterator<ICar> iterator = carLot.getCars().iterator();
            while (iterator.hasNext()) {
                ICar car = iterator.next();
                if (!shop.canFitAnotherCar()) {
                    break;
                }
                shop.addCar(car);
                iterator.remove();
                FileHandler.saveCarsToFile(lotName + ".txt", carLot.getCars(), false);
                FileHandler.updateLicensePlateLocation("licensePlates.txt", car.getPlate(), shop.getLocation());
            }
        }

        List<Object> transactionsSummary = FileHandler.getTransactionSummary(shop.getLocation() + "_transactions.txt");
        if (transactionsSummary != null) {
            double totalRevenue = (double) transactionsSummary.get(0);
            double totalDiscounts = (double) transactionsSummary.get(1);
            shop.setTotalRevenue(totalRevenue);
            shop.setTotalDiscounts(totalDiscounts);
        }

        List<ITransaction> transactions = FileHandler.getTransactionsFromFile(shop.getLocation() + "_transactions.txt");
        if (transactions != null) {
            for (ITransaction transaction : transactions) {
                shop.addTransaction(transaction);
            }
        }
    }

    public void rentCar(String type) {
        ICar car = shop.removeCarByType(type);
        boolean discountApplied = false;

        if (car != null) {
            System.out.println("Car " + car.getPlate() + " rented from local stock.");
        } else {
            System.out.println("Car of type " + type + " not in stock. Requesting from lots (discount will apply).");
            discountApplied = true;

            for (String lotName : allowedLots) {
                ICarLot carLot = new CarLot(lotName);
                FileHandler.loadFromFile(lotName + ".txt", carLot);

                car = carLot.removeCarByType(shop.getLocation(), type);
                if (car != null) {
                    System.out.println("Car " + car.getPlate() + " rented from lot: " + lotName);
                    FileHandler.saveCarsToFile(lotName + ".txt", carLot.getCars(), false);
                    break;
                }
            }

            if (car == null) {
                System.out.println("No car of type " + type + " available in any lot.");
                return;
            }
        }

        FileHandler.saveRentedCarsToFileAppend("rented_cars.txt", List.of(car), discountApplied);
        FileHandler.updateLicensePlateLocation("licensePlates.txt", car.getPlate(), "rented");
        System.out.println("Car " + car.getPlate() + " successfully rented.");

        System.out.println(balanceShopCars());
    }

    public void returnCar(String licensePlate, int kilometers) {
        if (licensePlate == null || licensePlate.isEmpty()) {
            System.out.println("Invalid license plate.");
            return;
        }
        if (kilometers < 0) {
            System.out.println("Invalid kilometers driven.");
            return;
        }

        List<ICar> rentedCars = FileHandler.getCarsFromFile("rented_cars.txt");
        if (rentedCars == null) {
            System.out.println("Error loading rented cars.");
            return;
        }

        ICar returningCar = null;
        for (ICar car : rentedCars) {
            if (car.getPlate().equals(licensePlate)) {
                returningCar = car;
                break;
            }
        }

        if (returningCar == null) {
            System.out.println("Car with license plate " + licensePlate + " not found in rented cars.");
            return;
        }

        boolean discountApplied = FileHandler.getDiscountApplied(licensePlate);

        rentedCars.remove(returningCar);
        FileHandler.removeRentedCarFromFile("rented_cars.txt", returningCar);

        double amount = shop.returnCar(returningCar, kilometers, discountApplied);
        double discountedAmount = 0.0;
        if (discountApplied) {
            discountedAmount = kilometers * 0.1;
        }
        ITransaction transaction = new Transaction(amount, discountApplied, returningCar, discountedAmount);
        shop.addTransaction(transaction);

        FileHandler.saveShopCarsToFile(shop.getLocation(), shop.getCars());
        FileHandler.updateLicensePlateLocation("licensePlates.txt", licensePlate, shop.getLocation());
        FileHandler.saveTransactionToFile(shop.getLocation() + "_transactions.txt", transaction);

        System.out.println("====================================");
        System.out.println("Car with license plate " + licensePlate + " successfully returned.");
        System.out.println("Total amount to pay: $" + amount);
        System.out.println("Discount applied: " + (discountApplied ? "Yes" : "No"));
        System.out.println("====================================");

        System.out.println(balanceShopCars());
    }

    public String balanceShopCars(){
        // Check if the shop has fewer than 2 empty spaces
        if (!shop.hasAtLeastTwoEmptySpaces()) {
            ICar carToMove = shop.getCars().get(0); // Select the first car in the shop
            String selectedLot = null;

            if (!allowedLots.isEmpty()) {
                selectedLot = allowedLots.get(0);
            } else {
                return "No allowed lots available";
            }

            ICarLot carLot = new CarLot(selectedLot);
            FileHandler.loadFromFile(selectedLot + ".txt", carLot);

            shop.getCars().remove(carToMove);
            carLot.addCar(carToMove);

            FileHandler.saveCarsToFile(selectedLot + ".txt", carLot.getCars(), false);
            FileHandler.saveShopCarsToFile(shop.getLocation(), shop.getCars());
            FileHandler.updateLicensePlateLocation("licensePlates.txt", carToMove.getPlate(), selectedLot);

            return "Car " + carToMove.getPlate() + " moved back to lot: " + selectedLot + " to keep 2 available spaces. \n";
        }

        // Check if the shop has more than 2 empty spaces
        if (shop.canFitAnotherCar()) {
            for (String lotName : allowedLots) {

                ICarLot carLot = new CarLot(lotName);
                FileHandler.loadFromFile(lotName + ".txt", carLot);

                if (!carLot.getCars().isEmpty()) {
                    ICar carToMove = carLot.getCars().get(0);
                    carLot.removeCar(carToMove.getPlate());
                    shop.addCar(carToMove);

                    FileHandler.saveCarsToFile(lotName + ".txt", carLot.getCars(), false);
                    FileHandler.saveShopCarsToFile(shop.getLocation(), shop.getCars());
                    FileHandler.updateLicensePlateLocation("licensePlates.txt", carToMove.getPlate(), shop.getLocation());

                    return "Car " + carToMove.getPlate() + " moved to shop from lot: " + lotName + " to maintain the shop full.\n";
                } else {
                    return "No cars available in lot: " + lotName  + "\n";
                }
            }

            return "No cars available in any allowed lot.";
        }
        return "";
    }

    public void listShop() {
        System.out.println(balanceShopCars());
        System.out.println("===================================");
        System.out.println("          Shop summary");
        System.out.println("===================================");
        System.out.println("Full Spaces: " + shop.getCars().size());
        System.out.println("Empty spaces: " + shop.availableSpaces());
        System.out.println("Total earned: $" + shop.getTotalRevenue());
        System.out.println("===================================");
        System.out.println("        Cars at " + shop.getLocation());
        System.out.println("===================================");
        for (ICar car : shop.getCars()) {
            System.out.println("- " + car.getType() + " | " + car.getPlate() + " | " + car.getMileage() + " km");
        }


    }

    public void showTransactions() {
        System.out.println("===================================");
        System.out.println("        Transactions summary");
        System.out.println("===================================");
        System.out.println("Total revenue: $" + shop.getTotalRevenue());
        System.out.println("Total discounts: $" + shop.getTotalDiscounts());
        if (shop.getTransactions() == null) {
            System.out.println("No transactions available.");
            return;
        }
        System.out.println("Total transactions: " + shop.getTransactions().size());
        System.out.println("===================================");
        System.out.println("        Transactions log");
        System.out.println("===================================");
        System.out.println("   Car  | Amount | Discount applied");
        for (ITransaction transaction : shop.getTransactions()) {
            System.out.println(transaction.getCar().getPlate() +
                               " | " + transaction.getAmount() +
                               "  | " + (transaction.isDiscountApplied() ? "Yes" : "No")); //
        }
    }

    public List<String> getAllowedLots() {
        return allowedLots;
    }

    /*
     * Prompt: Make a simple command line interface to interact with the user.
     * The interface should allow the user to rent a car, return a car, list the shop's cars,
     */
    public void runLoop() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String command;

        System.out.println("Welcome to the Shop Manager!");

        while (true) {
            System.out.println("");
            System.out.println("===================================");
            System.out.println("Available commands: rent, return, list, transactions, exit");
            System.out.print("Enter command: ");
            command = scanner.nextLine().trim().toLowerCase();
            System.out.println("===================================");

            switch (command) {
                case "rent":
                    System.out.print("Enter car type to rent: ");
                    String type = scanner.nextLine().trim();
                    rentCar(type);
                    break;

                case "return":
                    System.out.print("Enter car license plate: ");
                    String licensePlate = scanner.nextLine().trim().toUpperCase();
                    System.out.print("Enter kilometers driven: ");
                    int kilometers;
                    try {
                        kilometers = Integer.parseInt(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid kilometers. Please enter a number.");
                        break;
                    }
                    returnCar(licensePlate, kilometers);
                    break;

                case "list":
                    listShop();
                    break;

                case "transactions":
                    showTransactions();
                    break;

                case "exit":
                    System.out.println("Exiting Shop Manager. Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Unknown command. Please try again.");
            }
        }
    }
}

