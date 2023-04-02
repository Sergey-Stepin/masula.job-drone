package services.stepin.example.drone.service.exception;

public class LoadNotFoundException extends DroneException{

    public LoadNotFoundException(String message) {
        super(message);
    }

    public LoadNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
