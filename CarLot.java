import java.util.ArrayList;
import java.util.List;

public class CarLot implements ICarLot {
    private String lotName;
    private List<ICar> cars;

    public CarLot(String lotName) {
        this.lotName = lotName;
        this.cars = new ArrayList<>();
    }

    public void addCar(ICar car) {
        cars.add(car);
    }

    public void removeCar(String plate) {
        cars.removeIf(car -> car.getPlate().equals(plate));
    }

    public List<ICar> getCars() {
        return cars;
    }
}

