package services.stepin.example.drone.service.exception;

public class DroneException extends RuntimeException{
    public DroneException(String message) {
        super(message);
    }

    public DroneException(String message, Throwable cause) {
        super(message, cause);
    }
}
