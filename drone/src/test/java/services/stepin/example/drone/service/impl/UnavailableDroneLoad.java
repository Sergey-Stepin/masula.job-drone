package services.stepin.example.drone.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import services.stepin.example.drone.ResourceHelper;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Load;
import services.stepin.example.drone.model.Medication;
import services.stepin.example.drone.service.DroneService;
import services.stepin.example.drone.service.exception.DroneBatteryIsTooLow;
import services.stepin.example.drone.service.exception.DroneIsNotAvailable;
import services.stepin.example.drone.service.exception.InvalidLoadException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static services.stepin.example.drone.model.Drone.Model.CRUISERWEIGHT;
import static services.stepin.example.drone.model.Drone.Model.LIGHTWEIGHT;

@SpringBootTest
public class UnavailableDroneLoad {

    private final ResourceHelper resourceHelper = new ResourceHelper();

    @Autowired
    private DroneService droneService;

    @Test
    void givenDoubleLoad_shouldBadRequest() {

        Drone drone = new Drone();
        drone.setModel(CRUISERWEIGHT);
        drone.setSerialNumber("-0-e-FfONLIm;aelef)(NL;;ae");
        drone.setWeightLimitGram(444);
        drone.setBatteryLevel(80);
        Drone persistedDrone = droneService.register(drone);

        Load load = new Load();
        load.setDrone(persistedDrone);

        List<Medication> medications = new ArrayList<>();

        Medication medication1 = new Medication();
        medication1.setName("80_9ik-f3pkmlkUHOJB");
        medication1.setCode("AAA3A_77_0_98");
        medication1.setWeightGram(30);
        medications.add(medication1);
        load.setMedications(medications);

        Medication medication2 = new Medication();
        medication2.setName("pn6-K1knkjhl_B");
        medication2.setCode("XYZ_1");
        medication2.setWeightGram(20);
        medications.add(medication2);
        load.setMedications(medications);

        long droneId = persistedDrone.getDroneId();;

        droneService.load(droneId, load);

        assertThrows(
                DroneIsNotAvailable.class,
                () -> droneService.load(droneId, load));
    }

    @Test
    void givenBatteryLow_shouldBadRequest() {

        Drone drone = new Drone();
        drone.setModel(LIGHTWEIGHT);
        drone.setSerialNumber("-90io;lIHn--({J:Nrhr");
        drone.setWeightLimitGram(100);
        drone.setBatteryLevel(24);
        Drone persistedDrone = droneService.register(drone);

        Load load = new Load();
        load.setDrone(persistedDrone);

        List<Medication> medications = new ArrayList<>();

        Medication medication1 = new Medication();
        medication1.setName("80_9ik-f3pkmlkUHOJB");
        medication1.setCode("DJLK00__DD");
        medication1.setWeightGram(10);
        medications.add(medication1);
        load.setMedications(medications);

        long droneId = persistedDrone.getDroneId();

        assertThrows(
                DroneBatteryIsTooLow.class,
                () -> droneService.load(droneId, load));

    }

    @Test
    void givenTooHeavyLoad_shouldBadRequest() {

        Drone drone = new Drone();
        drone.setModel(LIGHTWEIGHT);
        drone.setSerialNumber("-_(Uj;lwkr78(GLKJ,masd");
        drone.setWeightLimitGram(50);
        drone.setBatteryLevel(25);
        Drone persistedDrone = droneService.register(drone);

        Load load = new Load();
        load.setDrone(persistedDrone);

        List<Medication> medications = new ArrayList<>();

        Medication medication1 = new Medication();
        medication1.setName("80_9ik-f3pkmlkUHOJB");
        medication1.setCode("AAA3A_77_0_98");
        medication1.setWeightGram(30);
        medications.add(medication1);
        load.setMedications(medications);

        Medication medication2 = new Medication();
        medication2.setName("pn6-K1knkjhl_B");
        medication2.setCode("XYZ_1");
        medication2.setWeightGram(21);
        medications.add(medication2);
        load.setMedications(medications);

        long droneId = persistedDrone.getDroneId();

        assertThrows(
                InvalidLoadException.class,
                () -> droneService.load(droneId, load));
    }
}
