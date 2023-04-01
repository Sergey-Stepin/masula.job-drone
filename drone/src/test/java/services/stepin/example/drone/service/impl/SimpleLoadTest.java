package services.stepin.example.drone.service.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import services.stepin.example.drone.ResourceHelper;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Load;
import services.stepin.example.drone.model.Medication;
import services.stepin.example.drone.repository.DroneRepository;
import services.stepin.example.drone.repository.LoadRepository;
import services.stepin.example.drone.service.DroneService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static services.stepin.example.drone.model.Drone.Model.*;

@SpringBootTest
class SimpleLoadTest {

    private final ResourceHelper resourceHelper = new ResourceHelper();

    @Autowired
    private DroneService droneService;

    @Autowired
    private DroneRepository droneRepository;

    @Autowired
    private LoadRepository loadRepository;

    private static Drone drone;

    @BeforeAll
    static void createDrone() {

        drone = new Drone();
        drone.setModel(MIDDLEWEIGHT);
        drone.setSerialNumber("xxx-1234-ABC_#01");
        drone.setWeightLimitGram(200);
    }

    @BeforeEach
    void prepareDatabase() {
        loadRepository.deleteAll();

        if(drone.getDroneId() == 0){
            droneRepository.save(drone);
        }
    }

    @Test
    void givenLoad_shouldPersist() {

        long droneId = drone.getDroneId();
        Load load = createSimpleLoad();

        Load persisted = droneService.load(droneId, load);

        checkLoad(persisted);
    }

    private Load createSimpleLoad() {

        Load load = new Load();
        load.setDrone(drone);

        List<Medication> medications = createSimpleMedicationList();
        load.setMedications(medications);

        return load;
    }

    private List<Medication> createSimpleMedicationList() {

        List<Medication> medications = new ArrayList<>();

        Medication medication1 = new Medication();
        medication1.setName("_xxx-321ALK_1");
        medication1.setCode("AAA_01");
        medication1.setWeightGram(100);
        medications.add(medication1);
        byte[] image1 = resourceHelper.getImage("med_1.jpg");
        medication1.setImage(image1);

        Medication medication2 = new Medication();
        medication2.setName("_xxx-098JBH_2");
        medication2.setCode("XYZ13");
        medication2.setWeightGram(200);
        medications.add(medication2);

        Medication medication3 = new Medication();
        medication3.setName("_abc-456KQP_3");
        medication3.setCode("013");
        medication3.setWeightGram(200);
        medications.add(medication3);
        byte[] image3 = resourceHelper.getImage("med_10.jpeg");
        medication3.setImage(image3);

        return medications;
    }

    private void checkLoad(Load persisted) {

        assertNotEquals(0, persisted.getLoadId());

        long loadId = persisted.getLoadId();
        Optional<Load> loadOptional = loadRepository.findById(loadId);

        assertTrue(loadOptional.isPresent());
        Load load = loadOptional.get();

        assertNotNull(load.getDrone());
        assertNotNull(load.getCreatedAt());
        assertNotNull(load.getMedications());

        assertNull(load.getLoadedAt());
        assertNull(load.getUnloadedAt());

        assertEquals(drone.getDroneId(), load.getDrone().getDroneId());

        List<Medication> medications = load.getMedications();

        checkFirstMedication(medications);
        checkSecondMedication(medications);
        checkThirdMedication(medications);
    }

    private void checkFirstMedication(List<Medication> medications) {

        Optional<Medication> optionalMedication = findMedicationInList(medications, "_xxx-321ALK_1");
        assertTrue(optionalMedication.isPresent());

        Medication medication = optionalMedication.get();
        assertEquals("AAA_01", medication.getCode());
        assertEquals(100, medication.getWeightGram());

        byte[] image = resourceHelper.getImage("med_1.jpg");
        assertArrayEquals(image, medication.getImage());
    }

    private void checkSecondMedication(List<Medication> medications) {

        Optional<Medication> optionalMedication = findMedicationInList(medications, "_xxx-098JBH_2");
        assertTrue(optionalMedication.isPresent());

        Medication medication = optionalMedication.get();
        assertEquals("XYZ13", medication.getCode());
        assertEquals(200, medication.getWeightGram());
        assertNull(medication.getImage(), " Expected image is null but actual is not null! ");

    }

    private void checkThirdMedication(List<Medication> medications) {

        Optional<Medication> optionalMedication = findMedicationInList(medications, "_abc-456KQP_3");
        assertTrue(optionalMedication.isPresent());

        Medication medication = optionalMedication.get();
        assertEquals("013", medication.getCode());
        assertEquals(200, medication.getWeightGram());

        byte[] image = resourceHelper.getImage( "med_10.jpeg");
        assertArrayEquals(image, medication.getImage());
    }

    private Optional<Medication> findMedicationInList(List<Medication> medications, String name) {
        return medications.stream()
                .filter(medication -> medication.getName().equals(name))
                .findAny();
    }
}