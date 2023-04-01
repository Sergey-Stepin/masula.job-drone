package services.stepin.example.drone.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.service.DroneService;
import services.stepin.example.drone.repository.DroneRepository;

@Service
@RequiredArgsConstructor
public class DroneServiceImpl implements DroneService {

    private final DroneRepository droneRepository;

    @Override
    public Drone register(Drone drone) {
        return droneRepository.save(drone);
    }

}
