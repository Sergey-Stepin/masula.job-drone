package services.stepin.example.drone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import services.stepin.example.drone.model.Load;

import java.util.Optional;

@Repository
public interface LoadRepository extends JpaRepository<Load, Long> {

    Optional<Load> findByDroneDroneIdAndUnloadedAtIsNull(long droneId);
}
