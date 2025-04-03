import java.util.List;

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
        FileHandler.saveToFile(lotFilename, carLot.getCars());
    }

    public void removeCar(String plate) {
        carLot.removeCar(plate);
        System.out.println("Removed: " + plate);
        FileHandler.saveToFile(lotFilename, carLot.getCars());
    }
}




