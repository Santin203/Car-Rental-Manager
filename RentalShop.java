import java.util.ArrayList;
import java.util.List;

public class RentalShop implements IRentalShop {
    private String location;
    private int maxSpaces;
    private List<ICar> cars;
    private double totalRevenue;
    private double totalDiscounts;
    private List<ITransaction> transactions;

    public RentalShop(String location, int maxSpaces) {
        this.location = location;
        this.maxSpaces = maxSpaces;
        this.cars = new ArrayList<>();
        this.totalRevenue = 0.0;
        this.totalDiscounts = 0.0;
        this.transactions = new ArrayList<>();
    }

    public boolean hasSpace() {
        return cars.size() < maxSpaces;
    }

    public boolean hasAtLeastTwoEmptySpaces() {
        return maxSpaces - cars.size() >= 2;
    }

    public boolean canFitAnotherCar() {
        return maxSpaces - cars.size() > 2;
    }

    public boolean hasAtLeastOneCar() {
        return !cars.isEmpty();
    }

    public int availableSpaces() {
        return maxSpaces - cars.size();
    }

    public void addCar(ICar car) {
        if (hasSpace()) {
            cars.add(car);
            FileHandler.saveShopCarsToFile(location, getCars());   
        }
    }

    public ICar removeCarByType(String type) {
        for (ICar car : cars) {
            if (car.getType().equalsIgnoreCase(type)) {
                cars.remove(car);
                FileHandler.saveShopCarsToFile(location, getCars());
                return car;
            }
        }
        return null;
    }

    public double returnCar(ICar car, int kilometers, boolean discountApplied) {
        car.updateMileage(kilometers);
        addCar(car);

        double amount = kilometers;
        if (discountApplied) {
            amount *= 0.9;
            totalDiscounts += kilometers * 0.1;
        }
        totalRevenue += amount;
        return amount;
    }

    public List<ICar> getCars() {
        return cars;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public double getTotalDiscounts() {
        return totalDiscounts;
    }

    public String getLocation() {
        return location;
    }

    public List<ITransaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(ITransaction transaction) {
        transactions.add(transaction);
    }
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    public void setTotalDiscounts(double totalDiscounts) {
        this.totalDiscounts = totalDiscounts;
    }
}

