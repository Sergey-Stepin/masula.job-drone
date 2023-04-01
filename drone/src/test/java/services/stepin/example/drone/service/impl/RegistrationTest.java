package services.stepin.example.drone.service.impl;


import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.repository.DroneRepository;
import services.stepin.example.drone.repository.LoadRepository;
import services.stepin.example.drone.service.DroneService;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static services.stepin.example.drone.model.Drone.Model.*;
import static services.stepin.example.drone.model.Drone.Model.LIGHTWEIGHT;
import static services.stepin.example.drone.model.State.*;

@SpringBootTest
class RegistrationTest {

    @Autowired
    private DroneService droneService;

    @Autowired
    private DroneRepository droneRepository;

    @Autowired
    private LoadRepository loadRepository;

    @BeforeEach
    void prepareDataBase() {
        loadRepository.deleteAll();
        droneRepository.deleteAll();
    }

    @Test
    void givenDrone_shouldRegister(){

        Drone.Model model = LIGHTWEIGHT;
        int weightLimitGram = 500;

        String serialNumber = new Random().ints()
                .limit(100)
                .mapToObj(i -> String.valueOf('0'))
                .collect(Collectors.joining());

        Drone drone = new Drone();
        drone.setModel(model);
        drone.setSerialNumber(serialNumber);
        drone.setWeightLimitGram(weightLimitGram);

        Drone persistedDrone = droneService.register(drone);

        assertNotEquals(0, persistedDrone.getDroneId());
        assertEquals(model, persistedDrone.getModel());
        assertEquals(IDLE, persistedDrone.getState());
        assertEquals(weightLimitGram, persistedDrone.getWeightLimitGram());
        assertEquals(serialNumber, persistedDrone.getSerialNumber());

        List<Drone> persistedDrones = droneRepository.findAll();
        assertEquals(1, persistedDrones.size());

    }

    @Test
    void givenBatteryLevel_Greater_100_shouldThrow(){

        Drone drone = new Drone();
        drone.setModel(HEAVYWEIGHT);
        drone.setSerialNumber("2");
        drone.setWeightLimitGram(1);
        drone.setBatteryLevel(101);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> droneService.register(drone));
        assertTrue(ex.getMessage().contains("batteryLevel"), " Exception does not mention <batteryLevel>");
    }

    @Test
    void givenBatteryLevel_Less_0_shouldThrow(){

        Drone drone = new Drone();
        drone.setModel(HEAVYWEIGHT);
        drone.setSerialNumber("2");
        drone.setWeightLimitGram(1);
        drone.setBatteryLevel(-1);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> droneService.register(drone));
        assertTrue(ex.getMessage().contains("batteryLevel"), " Exception does not mention <batteryLevel>");
    }

    @Test
    void givenWeightLimit_Greater_500_shouldThrow(){

        Drone drone = new Drone();
        drone.setModel(MIDDLEWEIGHT);
        drone.setSerialNumber("-");
        drone.setWeightLimitGram(501);
        drone.setState(DELIVERING);
        drone.setBatteryLevel(0);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> droneService.register(drone));
        assertTrue(ex.getMessage().contains("weightLimitGram"), " Exception does not mention <weightLimitGram>");
    }

    @Test
    void givenWeightLimit_Less_1_shouldThrow(){

        Drone drone = new Drone();
        drone.setModel(MIDDLEWEIGHT);
        drone.setSerialNumber("-");
        drone.setWeightLimitGram(0);
        drone.setState(DELIVERED);
        drone.setBatteryLevel(0);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> droneService.register(drone));
        assertTrue(ex.getMessage().contains("weightLimitGram"), " Exception does not mention <weightLimitGram>");
    }

    @Test
    void givenInvalidSerialNumber_shouldThrow(){

        String serialNumber = new Random().ints()
                .limit(101)
                .mapToObj(i -> String.valueOf('X'))
                .collect(Collectors.joining());

        Drone drone = new Drone();
        drone.setModel(LIGHTWEIGHT);
        drone.setSerialNumber(serialNumber);
        drone.setWeightLimitGram(500);
        drone.setState(LOADED);
        drone.setBatteryLevel(1);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> droneService.register(drone));
        assertTrue(ex.getMessage().contains("serialNumber"), " Exception does not mention <serialNumber>");
    }

    @Test
    void givenEmptySerialNumber_shouldThrow(){

        Drone drone = new Drone();
        drone.setModel(LIGHTWEIGHT);
        drone.setSerialNumber("");
        drone.setWeightLimitGram(500);
        drone.setState(LOADING);
        drone.setBatteryLevel(100);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> droneService.register(drone));
        assertTrue(ex.getMessage().contains("serialNumber"), " Exception does not mention <serialNumber>");
    }

}