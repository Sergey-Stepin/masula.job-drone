package services.stepin.example.drone.service.exception;

public class DroneNotFoundException extends DroneException{
    public DroneNotFoundException(String message) {
        super(message);
    }

    public DroneNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
