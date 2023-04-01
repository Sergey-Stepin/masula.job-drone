package services.stepin.example.drone.service.exception;

public class InvalidMedicineException extends DroneException{
    public InvalidMedicineException(String message) {
        super(message);
    }

    public InvalidMedicineException(String message, Throwable cause) {
        super(message, cause);
    }
}
