package services.stepin.example.drone.controller;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.stepin.example.drone.controller.dto.DroneDto;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.service.DroneService;
import services.stepin.example.drone.service.exception.DroneException;

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

}
