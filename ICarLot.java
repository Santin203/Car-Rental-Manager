import java.util.List;

public interface ICarLot {
    void addCar(ICar car);
    void removeCar(String plate);
    List<ICar> getCars();
}
