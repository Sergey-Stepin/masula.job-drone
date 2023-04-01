package services.stepin.example.drone.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Load {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long loadId;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Drone drone;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    private OffsetDateTime loadedAt;

    private OffsetDateTime unloadedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Medication> medications;

    @Override
    public String toString() {
        return "Load{" +
                "loadId=" + loadId +
                ", drone=" + drone +
                ", createdAt=" + createdAt +
                ", loadedAt=" + loadedAt +
                ", unloadedAt=" + unloadedAt +
                ", medications=" + medications +
                '}';
    }
}
