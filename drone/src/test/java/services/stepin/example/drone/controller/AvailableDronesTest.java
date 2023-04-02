package services.stepin.example.drone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import services.stepin.example.drone.controller.dto.DroneDto;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.repository.DroneRepository;
import services.stepin.example.drone.repository.LoadRepository;
import services.stepin.example.drone.service.DroneService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static services.stepin.example.drone.model.Drone.Model;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static services.stepin.example.drone.model.Drone.Model.*;
import static services.stepin.example.drone.model.State.*;

@SpringBootTest
@AutoConfigureMockMvc
class AvailableDronesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DroneService droneService;

    @Autowired
    private DroneRepository droneRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private LoadRepository loadRepository;

    @BeforeEach
    void prepareDataBase() {
        loadRepository.deleteAll();
        droneRepository.deleteAll();
    }

    @Test
    void givenListOfDrones_shouldReturnAvailable() throws Exception {

        Drone drone1 = createAndRegisterDrone("a-1", LIGHTWEIGHT, 100);
        Drone drone2 = createAndRegisterDrone("b-2", MIDDLEWEIGHT, 250);
        Drone drone3 = createAndRegisterDrone("c-3", MIDDLEWEIGHT, 280);
        Drone drone4 = createAndRegisterDrone("d-4", CRUISERWEIGHT, 300);
        Drone drone5 = createAndRegisterDrone("e-5", HEAVYWEIGHT, 400);

        checkAvailable(getAvailable(), drone1, drone2, drone3, drone4, drone5);

        droneService.updateState(drone2.getDroneId(), LOADING);
        droneService.updateState(drone3.getDroneId(), LOADED);

        checkUnavailable(getAvailable(), drone2, drone3);
        checkAvailable(getAvailable(), drone1, drone4, drone5);

        droneService.updateState(drone3.getDroneId(), RETURNING);
        droneService.updateState(drone4.getDroneId(), DELIVERING);
        droneService.updateState(drone5.getDroneId(), DELIVERED);

        checkUnavailable(getAvailable(), drone2, drone4, drone5);
        checkAvailable(getAvailable(), drone1, drone3);

}

    private void checkAvailable(List<DroneDto> available, Drone ...  drones) {

        Optional<Drone> notMatch = Arrays.stream(drones)
                .filter(drone -> !checkMatch(drone, available))
                        .findAny();

        assertTrue(notMatch.isEmpty(), () -> String.format(
                " Drone is expected to be Available but it is not! droneId: %s",
                notMatch.get().getDroneId()));
    }

    private void checkUnavailable(List<DroneDto> available, Drone ...  drones) {

        Optional<Drone> notMatch = Arrays.stream(drones)
                .filter(drone -> checkMatch(drone, available))
                .findAny();

        assertTrue(notMatch.isEmpty(), () -> String.format(
                " Drone is expected to be Unavailable but it is not! droneId: %s",
                notMatch.get().getDroneId()));
    }

    private boolean checkMatch(Drone drone, List<DroneDto> available){
        return available.stream()
                .anyMatch(availableDrone -> availableDrone.getDroneId() == drone.getDroneId());
    }

    private Drone createAndRegisterDrone(String serialNumber, Model model, int weightLimit) throws Exception {
        Drone drone = new Drone();
        drone.setModel(model);
        drone.setSerialNumber(serialNumber);
        drone.setWeightLimitGram(weightLimit);

        DroneDto requestDto = DroneDto.toDto(drone);

        System.out.println("### " + drone);
        MvcResult mvcResult = mockMvc.perform(post("/register")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String stingResponse = mvcResult.getResponse().getContentAsString();
        DroneDto responseDto = mapper.readValue(stingResponse, DroneDto.class);

        drone.setDroneId(responseDto.getDroneId());

        return drone;
    }

    private List<DroneDto> getAvailable() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/available")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String stingResponse = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(stingResponse, new TypeReference<List<DroneDto>>() { });
    }


}