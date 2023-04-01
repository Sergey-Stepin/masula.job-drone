package services.stepin.example.drone.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Drone.Model;
import services.stepin.example.drone.model.Drone.State;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroneDto {

    private long droneId;

    private String serialNumber;

    private String model;

    private int weightLimitGram;

    private int batteryLevel;

    private String state;

    public static Drone fromDto(DroneDto dto){

        Drone drone = new Drone();
        drone.setDroneId(dto.droneId);
        drone.setModel(Model.valueOf(dto.model));
        drone.setState(State.valueOf(dto.state));
        drone.setSerialNumber(dto.serialNumber);
        drone.setWeightLimitGram(dto.weightLimitGram);
        drone.setBatteryLevel(dto.batteryLevel);

        return drone;
    }

    public static DroneDto toDto(Drone drone){

        return DroneDto.builder()
                .droneId(drone.getDroneId())
                .model(drone.getModel().name())
                .state(drone.getState().name())
                .serialNumber(drone.getSerialNumber())
                .weightLimitGram(drone.getWeightLimitGram())
                .batteryLevel(drone.getBatteryLevel())
                .build();
    }

    }
