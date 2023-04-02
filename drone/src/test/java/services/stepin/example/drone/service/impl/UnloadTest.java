package services.stepin.example.drone.service.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Load;
import services.stepin.example.drone.model.Medication;
import services.stepin.example.drone.repository.LoadRepository;
import services.stepin.example.drone.service.DroneService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static services.stepin.example.drone.model.Drone.Model.*;

@SpringBootTest
class UnloadTest {

    @Autowired
    private DroneService droneService;

    @Autowired
    private LoadRepository loadRepository;

    private static Drone drone;

    @BeforeAll
    static void createDrone() {

        drone = new Drone();
        drone.setModel(CRUISERWEIGHT);
        drone.setSerialNumber("_xx23(*)-pTRVk");
        drone.setWeightLimitGram(100);
    }

    @BeforeEach
    void prepareDatabase() {

        if(drone.getDroneId() == 0){
            droneService.register(drone);
        }
    }

    @Test
    void givenLoad_shouldUnload(){

        Load load = createAndload();

        Load persistedLoad = findLoad(load);
        assertNull(persistedLoad.getUnloadedAt());

        unload(load);

        Load loadAfterUnloading = findLoad(load);
        assertNotNull(loadAfterUnloading.getUnloadedAt());

        //repeatable unload
        assertThrows(
                IllegalArgumentException.class,
                () -> unload(load));
    }

    private Load createAndload(){
        Load load = new Load();
        load.setDrone(drone);

        List<Medication> medications = new ArrayList<>();

        Medication medication1 = new Medication();
        medication1.setName("WWW");
        medication1.setCode("0987_M1");
        medication1.setWeightGram(20);
        medications.add(medication1);
        load.setMedications(medications);

        Medication medication2 = new Medication();
        medication2.setName("XXX");
        medication2.setCode("FFF_M2");
        medication2.setWeightGram(10);
        medications.add(medication2);
        load.setMedications(medications);

        droneService.load(drone.getDroneId(), load);

        return load;
    }

    private void unload(Load load){
        droneService.unload(load.getLoadId());
    }

    private Load findLoad(Load load){
        Optional<Load> foundLoad = loadRepository.findById(load.getLoadId());

        assertTrue(foundLoad.isPresent(), " Load nof found, loadId=" + load.getLoadId());

        return foundLoad.get();
    }

}