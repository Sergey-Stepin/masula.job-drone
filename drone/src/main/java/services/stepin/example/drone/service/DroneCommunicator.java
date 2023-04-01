package services.stepin.example.drone.service;

import services.stepin.example.drone.model.Drone;

public interface DroneCommunicator {

    int getBatteryLevel(Drone drone);
}
