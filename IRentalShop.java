import java.util.List;

public interface IRentalShop {
    String getLocation();
    List<ICar> getCars();
    double getTotalRevenue();
    double getTotalDiscounts();
    List<ITransaction> getTransactions();
    boolean hasSpace();
    boolean hasAtLeastTwoEmptySpaces();
    boolean canFitAnotherCar();
    boolean hasAtLeastOneCar();
    int availableSpaces();
    void addCar(ICar car);
    ICar removeCarByType(String type);
    double returnCar(ICar car, int kilometers, boolean discountApplied);
    void addTransaction(ITransaction transaction);
}
