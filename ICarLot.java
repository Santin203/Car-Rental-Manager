import java.util.List;

public interface ICarLot {
    void addCar(ICar car);
    boolean removeCar(String plate);
    List<ICar> getCars();
    ICar removeCarByType(String location, String type);
}
