package services.stepin.example.drone.service.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Load;
import services.stepin.example.drone.model.Medication;
import services.stepin.example.drone.service.DroneService;
import services.stepin.example.drone.service.exception.InvalidMedicineException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static services.stepin.example.drone.model.Drone.Model.HEAVYWEIGHT;

@SpringBootTest
public class LoadInvalidMedicationTest {

    @Autowired
    private DroneService droneService;

    private static Drone drone;

    @BeforeAll
    static void createDrone() {

        drone = new Drone();
        drone.setModel(HEAVYWEIGHT);
        drone.setSerialNumber("4GG*_)_ower90hbn_(_Nkj_#01");
        drone.setWeightLimitGram(400);
    }

    @BeforeEach
    void prepareDatabase() {

        if(drone.getDroneId() == 0){
            droneService.register(drone);
        }
    }

    @Test
    public void givenInvalidCode_shouldThrow(){

        Load load = createWithInvalidCode();

        InvalidMedicineException ex = assertThrows(
                InvalidMedicineException.class,
                () -> droneService.load(drone.getDroneId(), load));

        assertTrue(ex.getMessage().contains("Code does not match the pattern"), "wrong exception message");

    }

    @Test
    public void givenInvalidWeight_shouldThrow(){

        Load load = createWithInvalidWeight();

        InvalidMedicineException ex = assertThrows(
                InvalidMedicineException.class,
                () -> droneService.load(drone.getDroneId(), load));

        assertTrue(ex.getMessage().contains("weightGram"), " Exception does not mention <weightGram>");

    }

    @Test
    public void givenInvalidName_shouldThrow(){

        Load load = createWithInvalidName();

        InvalidMedicineException ex = assertThrows(
                InvalidMedicineException.class,
                () -> droneService.load(drone.getDroneId(), load));

        assertTrue(ex.getMessage().contains("Name does not match the pattern"), "wrong exception message");

    }

    private Load createWithInvalidCode() {

        Load load = new Load();
        load.setDrone(drone);

        List<Medication> medications = new ArrayList<>();

        Medication medication = new Medication();
        medication.setName("01");
        medication.setCode("098XXX-11");
        medication.setWeightGram(400);
        medications.add(medication);
        load.setMedications(medications);

        return load;
    }

    private Load createWithInvalidName() {

        Load load = new Load();
        load.setDrone(drone);

        List<Medication> medications = new ArrayList<>();

        Medication medication = new Medication();
        medication.setName("01*");
        medication.setCode("098XXX11");
        medication.setWeightGram(400);
        medications.add(medication);
        load.setMedications(medications);

        return load;
    }

    private Load createWithInvalidWeight() {

        Load load = new Load();
        load.setDrone(drone);

        List<Medication> medications = new ArrayList<>();

        Medication medication = new Medication();
        medication.setName("01");
        medication.setCode("098XXX11");
        medication.setWeightGram(0);
        medications.add(medication);
        load.setMedications(medications);

        return load;
    }
}
