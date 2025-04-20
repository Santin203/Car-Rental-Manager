public class LotManager implements ILotManager {
    private String lotFilename;
    private String lotName;
    private ICarLot carLot;

    public LotManager(String lotName) {
        this.lotName = lotName;
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
        FileHandler.saveCarsToFile(lotFilename, carLot.getCars(), false);
        FileHandler.saveLicensePlates("licensePlates.txt", carLot.getCars(), lotName);
    }
    
    public void removeCar(String plate) {
        carLot.removeCar(plate);
        System.out.println("Removed: " + plate);
        FileHandler.saveCarsToFile(lotFilename, carLot.getCars(), false);
        FileHandler.saveLicensePlates("licensePlates.txt", carLot.getCars(), lotName);
        FileHandler.removeLicensePlate("licensePlates.txt", plate);
    }
}




