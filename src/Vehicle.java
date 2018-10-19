public class Vehicle {
    private String name;
    private boolean parkingFlag;
    private String registrationForm;
    private int volume;
    private int speed;

    public Vehicle(String name, boolean parkingFlag, String registrationForm, int volume, int speed) {
        this.name = name;
        this.parkingFlag = parkingFlag;
        this.registrationForm = registrationForm;
        this.volume = volume;
        this.speed = speed;
    }
}
