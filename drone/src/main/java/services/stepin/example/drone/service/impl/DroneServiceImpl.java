package services.stepin.example.drone.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Load;
import services.stepin.example.drone.model.Medication;
import services.stepin.example.drone.repository.LoadRepository;
import services.stepin.example.drone.service.DroneService;
import services.stepin.example.drone.repository.DroneRepository;
import services.stepin.example.drone.service.exception.DroneNotFoundException;
import services.stepin.example.drone.service.exception.InvalidLoadException;

import java.util.List;
import java.util.Optional;

import static services.stepin.example.drone.model.Drone.State.RETURNING;

@Service
@RequiredArgsConstructor
public class DroneServiceImpl implements DroneService {

    private final DroneRepository droneRepository;
    private final LoadRepository loadRepository;

    @Override
    public Drone register(Drone drone) {
        return droneRepository.save(drone);
    }

    @Override
    public Load load(long droneId, Load load) {

        Drone drone = findById(droneId);

        load.setDrone(drone);

        validateLoad(load);

        return loadRepository.save(load);
    }

    @Override
    public Drone getWithLoad(long droneId) {

        Drone drone = findById(droneId);

        Optional<Load> load =  loadRepository.findByDroneDroneIdAndUnloadedAtIsNull(droneId);
        load.ifPresent(drone::setLoad);

        return drone;
    }

    @Override
    public void updateState(long droneId, Drone.State state) {

        Drone drone = findById(droneId);
        drone.setState(state);
        droneRepository.save(drone);
    }

    @Override
    public List<Drone> available() {
        return droneRepository.findByStateIn(Drone.State.IDLE, RETURNING);
    }

    @Override
    public Drone findById(long droneId) {
        Optional<Drone> drone = droneRepository.findById(droneId);
        return drone.orElseThrow(() -> new DroneNotFoundException("droneId: " + droneId));

    }

    @Override
    public List<Drone> findALl() {
        return  droneRepository.findAll();
    }

    private void validateLoad(Load load){

        load.getMedications()
                .forEach(Medication::validate);
    }

}
