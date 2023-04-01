package services.stepin.example.drone.service;

import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Load;
import services.stepin.example.drone.model.State;

import java.util.List;

public interface DroneService {

    Drone register(Drone drone);

    Load load(long droneId, Load load);

    Drone getWithLoad(long droneId);

    void updateState(long droneId, State state);

    List<Drone> available();

    boolean isAvailable(long droneId);

    Drone findById(long droneId);

    List<Drone> findALl();

    void updateBatteryLevel(long droneId, int batteryLevel);
}
