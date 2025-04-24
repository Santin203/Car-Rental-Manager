 public class Transaction implements ITransaction {
    private double amount;
    private Boolean discountApplied;
    private double discountedAmount;
    private ICar car;

    public Transaction(double amount, Boolean discountApplied, ICar car, double discountedAmount) {
        this.amount = amount;
        this.discountApplied = discountApplied;
        this.discountedAmount = discountedAmount;
        this.car = car;
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public Boolean isDiscountApplied() {
        return discountApplied;
    }

    @Override
    public ICar getCar() {
        return car;
    }

    @Override
    public double getDiscountedAmount() {
        return discountedAmount;
    }
}