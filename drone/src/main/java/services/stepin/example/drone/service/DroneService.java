package services.stepin.example.drone.service;

import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Load;

import java.util.List;

public interface DroneService {

    Drone register(Drone drone);

    Load load(long droneId, Load load);

    Drone getWithLoad(long droneId);

    void updateState(long droneId, Drone.State state);

    List<Drone> available();

    Drone findById(long droneId);

    List<Drone> findALl();

    void updateBatteryLevel(long droneId, int batteryLevel);
}
