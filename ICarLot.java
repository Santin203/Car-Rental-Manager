import java.util.List;

public interface ICarLot {
    void addCar(ICar car);
    void removeCar(String plate);
    List<ICar> getCars();
    ICar removeCarByType(String location, String type);
}
