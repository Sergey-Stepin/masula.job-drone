package services.stepin.example.drone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.service.DroneCommunicator;
import services.stepin.example.drone.service.DroneMonitor;
import services.stepin.example.drone.service.DroneService;

import static java.lang.String.format;

@Service
@Log4j2
@RequiredArgsConstructor
public class DroneMonitorImpl implements DroneMonitor {

    private final DroneCommunicator droneCommunicator;
    private final DroneService droneService;

    @Override
    @Scheduled(fixedRateString = "${drones-monitor.rate-in-milliseconds}")
    public void monitorAll() {

        droneService.findALl()
                .forEach(this::monitor);
    }

    private void monitor(Drone drone){

        int batteryLevel = droneCommunicator.getBatteryLevel(drone);
        log(drone, batteryLevel);
        droneService.updateBatteryLevel(drone.getDroneId(), batteryLevel);
    }

    private void log(Drone drone, int batteryLevel){
        log.info(format("id=%d; serial_number=%s; battery_level=%d ",
                drone.getDroneId(),
                drone.getSerialNumber(),
                batteryLevel));
    }
}
