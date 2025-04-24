import java.util.List;

public interface IShopManager {
    void loadShop();
    void rentCar(String type);
    void returnCar(String licensePlate, int kilometers);
    String balanceShopCars();
    void listShop();
    void showTransactions();
    List<String> getAllowedLots();
    void runLoop();
}
