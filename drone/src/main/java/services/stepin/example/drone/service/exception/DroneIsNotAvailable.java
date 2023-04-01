package services.stepin.example.drone.service.exception;

public class DroneIsNotAvailable extends DroneException {
    public DroneIsNotAvailable(String message) {
        super(message);
    }

    public DroneIsNotAvailable(String message, Throwable cause) {
        super(message, cause);
    }
}
