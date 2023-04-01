package services.stepin.example.drone.service;

import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Drone.State;

import java.util.List;

public interface DroneService {

    Drone register(Drone drone);

}
