package services.stepin.example.drone.service.exception;

public class InvalidLoadException extends DroneException{
    public InvalidLoadException(String message) {
        super(message);
    }

    public InvalidLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
