package services.stepin.example.drone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.State;

import java.util.Collection;
import java.util.List;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {

    List<Drone> findByStateIn(Collection<State> states);
}
