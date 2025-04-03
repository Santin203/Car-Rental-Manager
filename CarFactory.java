public class CarFactory {
    public static ICar createCar(String type) {
        String plate = LicensePlateGenerator.generate();
        return new Car(plate, type);
    }
}

