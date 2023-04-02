package services.stepin.example.drone.controller;

import jakarta.validation.ValidationException;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.stepin.example.drone.controller.dto.DroneDto;
import services.stepin.example.drone.controller.dto.LoadDto;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Load;
import services.stepin.example.drone.service.DroneService;
import services.stepin.example.drone.service.exception.DroneException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DroneController {

    private final DroneService droneService;

    @ExceptionHandler({ValidationException.class, DroneException.class})
    public ResponseEntity<String> handleDataAccessException(Exception ex) {

        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }

    @PostMapping("/register")
    public ResponseEntity<DroneDto> register(@RequestBody DroneDto dto){

        Drone drone = DroneDto.fromDto(dto);
        Drone registeredDrone = droneService.register(drone);
        DroneDto registeredDto = DroneDto.toDto(registeredDrone);

        return ResponseEntity
                .ok(registeredDto);
    }

    @GetMapping("/list")
    public ResponseEntity<List<DroneDto>> list(){
        List<DroneDto> drones = droneService.findALl()
                .stream()
                .map(DroneDto::toDto)
                .toList();

        return ResponseEntity.ok(drones);
    }

    @PostMapping("/load")
    public ResponseEntity<LoadDto> load(@RequestBody LoadDto dto){

        Load load = LoadDto.fromDto(dto);
        Load persistedLoad = droneService.load(dto.getDroneId(), load);
        LoadDto persistedDto = LoadDto.toDto(persistedLoad);

        return ResponseEntity.ok(persistedDto);
    }

    @GetMapping("/drone_with_load")
    public ResponseEntity<DroneDto> getWithLoad(@PathParam("droneId") long droneId){

        Drone drone = droneService.getWithLoad(droneId);
        DroneDto droneDto = DroneDto.toDto(drone);

        return ResponseEntity.ok(droneDto);
    }

    @GetMapping("/available")
    public ResponseEntity<List<DroneDto>> available(){

        List<DroneDto> dtoList = droneService.available()
                .stream()
                .map(DroneDto::toDto)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/battery_level")
    public ResponseEntity<Integer> getBatteryLevel(@PathParam("droneId") long droneId){
        int batteryLevel = droneService.getBatteryLevel(droneId);
        return ResponseEntity.ok(batteryLevel);
    }
}
