package services.stepin.example.drone.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Load;
import services.stepin.example.drone.model.Medication;
import services.stepin.example.drone.model.State;
import services.stepin.example.drone.repository.LoadRepository;
import services.stepin.example.drone.service.DroneService;
import services.stepin.example.drone.repository.DroneRepository;
import services.stepin.example.drone.service.exception.*;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DroneServiceImpl implements DroneService {

    private final DroneRepository droneRepository;
    private final LoadRepository loadRepository;

    @Value("${drone.battery-level.minimum-for-load:25}")
    private int batteryLevelMinimumForLoad;

    @Override
    public Drone register(Drone drone) {
        return droneRepository.save(drone);
    }

    @Override
    public Load load(long droneId, Load load) {

        Drone drone = findById(droneId);
        load.setDrone(drone);

        checkAvailability(drone, load);
        validateLoad(load);

        return loadRepository.save(load);
    }

    @Override
    public void unload(long loadId) {

        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new LoadNotFoundException(" loadId: " + loadId));

        if(load.getUnloadedAt() != null){
            throw new IllegalArgumentException(String.format(
                    "Cannot unload: the load (loadId: %s) is already unloaded ", loadId));
        }

        load.setUnloadedAt(OffsetDateTime.now());
        loadRepository.save(load);
    }

    @Override
    public Drone getWithLoad(long droneId) {

        Drone drone = findById(droneId);

        Optional<Load> load =  loadRepository.findByDroneDroneIdAndUnloadedAtIsNull(droneId);
        load.ifPresent(drone::setLoad);

        return drone;
    }

    @Override
    public void updateState(long droneId, State state) {

        Drone drone = findById(droneId);
        drone.setState(state);
        droneRepository.save(drone);
    }

    @Override
    public List<Drone> available() {

        Collection<State> states = State.availableStates();
        return droneRepository.findByStateIn(states);
    }

    @Override
    public boolean isAvailable(long droneId) {

        Drone drone = findById(droneId);

        State state = drone.getState();
        return State.isAvailable(state);
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

    @Override
    public void updateBatteryLevel(long droneId, int batteryLevel) {
        Drone foundDrone = findById(droneId);
        foundDrone.setBatteryLevel(batteryLevel);
        droneRepository.save(foundDrone);
    }

    private void validateLoad(Load load){

        List<Medication> medications = load.getMedications();

        if(medications == null || medications.isEmpty()){
            throw new InvalidLoadException(String.format(
                    "loadId: %d : medication list is empty! ", load.getLoadId()));
        }

        load.getMedications()
                .forEach(Medication::validate);
    }

    private void checkAvailability(Drone drone, Load load){

        checkAvailability(drone);
        checkHasCurrentLoad(drone);
        checkBatteryLevel(drone);
        checkWeight(drone, load);
    }

    private void checkAvailability(Drone drone){

        long droneId = drone.getDroneId();
        if( !isAvailable(droneId)){
            throw new DroneIsNotAvailable(String.format(
                    "The drone (droneId: %s, status: %s) is not available! !",
                    droneId,
                    drone.getState()));
        }
    }

    private void checkHasCurrentLoad(Drone drone){
        long droneId = drone.getDroneId();
        if (hasLoad(droneId)) {
            throw new DroneIsNotAvailable(String.format(
                    "The drone (droneId: %s, status: %s): Has already a load !",
                    droneId,
                    drone.getState()));
        }
    }

    private void checkBatteryLevel(Drone drone){
        long droneId = drone.getDroneId();
        if (drone.getBatteryLevel() < batteryLevelMinimumForLoad) {
            throw new DroneBatteryIsTooLow(String.format(
                    "The drone (droneId: %s, status: %s): Battery level is too low !",
                    droneId,
                    drone.getState()));
        }
    }

    private void checkWeight(Drone drone, Load load){
        int loadWeight = getTotalWeight(load);
        if(drone.getWeightLimitGram() < loadWeight){
            throw new InvalidLoadException(String.format(
                    "The load is to heavy for the drone: droneId: %s, weight_limit: %d, total load weight: %d",
                    drone.getDroneId(),
                    drone.getWeightLimitGram(),
                    loadWeight));
        }
    }


    private boolean hasLoad(long droneId){
        return loadRepository.findByDroneDroneIdAndUnloadedAtIsNull(droneId)
                .isPresent();
    }

    private int getTotalWeight(Load load){
        return load.getMedications()
                .stream()
                .mapToInt(Medication::getWeightGram)
                .sum();
    }

}
