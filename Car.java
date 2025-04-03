public class Car implements ICar {
    private String plate;
    private String type;
    private int mileage;

    public Car(String plate, String type) {
        this.plate = plate;
        this.type = type;
        this.mileage = 0;
    }

    public String getPlate() {
        return plate;
    }

    public String getType() {
        return type;
    }

    public int getMileage() {
        return mileage;
    }

    public void updateMileage(int km) {
        this.mileage += km;
    }
}

