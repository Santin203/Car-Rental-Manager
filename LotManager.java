public class LotManager implements ILotManager {
    private String lotFilename;
    private ICarLot carLot;

    public LotManager(String lotName) {
        this.lotFilename = lotName + ".txt";
        this.carLot = new CarLot(lotFilename);
        FileHandler.loadFromFile(lotFilename, carLot);
    }

    public void addCars(String type, int count) {
        for (int i = 0; i < count; i++) {
            ICar newCar = CarFactory.createCar(type);
            carLot.addCar(newCar);
            System.out.println("Added: " + newCar.getPlate() + " (" + type + ")");
        }
        FileHandler.saveCarsToFile(lotFilename, carLot.getCars());
        FileHandler.saveLicensePlates("licensePlates.txt", carLot.getCars(), lotFilename);
    }
    
    public void removeCar(String plate) {
        carLot.removeCar(plate);
        System.out.println("Removed: " + plate);
        FileHandler.saveCarsToFile(lotFilename, carLot.getCars());
        FileHandler.saveLicensePlates("licensePlates.txt", carLot.getCars(), lotFilename);

        // Remove the license plate from licensePlates.txt if it is not in use
        FileHandler.removeLicensePlate("licensePlates.txt", plate);
    }
}




