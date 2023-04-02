package services.stepin.example.drone.service;

import services.stepin.example.drone.model.Drone;

public interface DroneMonitor {

    void monitorAll();

    void monitor(Drone drone);
}
