package services.stepin.example.drone.service.exception;

public class DroneBatteryIsTooLow extends DroneException{
    public DroneBatteryIsTooLow(String message) {
        super(message);
    }

    public DroneBatteryIsTooLow(String message, Throwable cause) {
        super(message, cause);
    }
}
