package services.stepin.example.drone.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.repository.DroneRepository;
import services.stepin.example.drone.service.DroneCommunicator;

import java.util.Random;

@Service
@AllArgsConstructor
    public class MockDroneCommunicator implements DroneCommunicator {

    private final DroneRepository droneRepository;
    @Override
    public int getBatteryLevel(Drone drone) {
        return droneRepository.findById(drone.getDroneId())
                .get()
                .getBatteryLevel();
    }
}
