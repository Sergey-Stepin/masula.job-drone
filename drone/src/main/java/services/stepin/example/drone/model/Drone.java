package services.stepin.example.drone.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import static services.stepin.example.drone.model.Drone.State.IDLE;

@Entity
@Getter
@Setter
public class Drone {

    public enum Model{ LIGHTWEIGHT, MIDDLEWEIGHT, CRUISERWEIGHT, HEAVYWEIGHT}
    public enum State {IDLE, LOADING, LOADED, DELIVERING, DELIVERED, RETURNING}

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long droneId;

    @NotNull
    @NotEmpty
    @Size(max = 100)
    private String serialNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Model model;

    @NotNull
    @Max(500)
    @Min(1)
    private int weightLimitGram;

    @NotNull
    @Max(100)
    @PositiveOrZero
    private int batteryLevel = 100;

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state = IDLE;

    @Transient
    private Load load;

    @Override
    public String toString() {
        return "Drone{" +
                "droneId=" + droneId +
                ", serialNumber='" + serialNumber + '\'' +
                ", model=" + model +
                ", weightLimitGram=" + weightLimitGram +
                ", batteryLevel=" + batteryLevel +
                ", state=" + state +
                ", load=" + load +
                '}';
    }
}
