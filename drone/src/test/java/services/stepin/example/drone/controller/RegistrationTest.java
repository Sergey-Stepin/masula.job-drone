package services.stepin.example.drone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static services.stepin.example.drone.model.Drone.Model.*;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static services.stepin.example.drone.model.Drone.State.*;


@SpringBootTest
@AutoConfigureMockMvc
class RegistrationTest {

    @Autowired
    private MockMvc mockMvc;

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
    void givenDrone_shouldRegister() throws Exception {

        Drone.Model model = HEAVYWEIGHT;
        String serialNumber = "ssssABC-1";
        int weightLimitGram = 450;

        Drone drone = new Drone();
        drone.setModel(model);
        drone.setSerialNumber(serialNumber);
        drone.setWeightLimitGram(weightLimitGram);

        DroneDto requestDto = DroneDto.toDto(drone);

        MvcResult mvcResult = mockMvc.perform(post("/register")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String stingResponse = mvcResult.getResponse().getContentAsString();
        DroneDto responseDto = mapper.readValue(stingResponse, DroneDto.class);
        assertNotEquals(0, responseDto.getDroneId());
        assertEquals(model.name(), responseDto.getModel());
        assertEquals(IDLE.name(), responseDto.getState());
        assertEquals(weightLimitGram, responseDto.getWeightLimitGram());
        assertEquals(serialNumber, responseDto.getSerialNumber());


        List<Drone> persistedDrones = droneRepository.findAll();
        Drone persistedDrone = persistedDrones.get(0);

        assertEquals(1, persistedDrones.size());
        assertNotEquals(0, persistedDrone.getDroneId());
        assertEquals(model, persistedDrone.getModel());
        assertEquals(IDLE, persistedDrone.getState());
        assertEquals(weightLimitGram, persistedDrone.getWeightLimitGram());
        assertEquals(serialNumber, persistedDrone.getSerialNumber());

    }

    @Test
    void givenInvalidDrone_shouldBadRequest() throws Exception {

        Drone drone = new Drone();
        drone.setModel(CRUISERWEIGHT);
        drone.setSerialNumber("");
        drone.setWeightLimitGram(500);
        drone.setBatteryLevel(100);


        DroneDto requestDto = DroneDto.toDto(drone);
        mockMvc.perform(post("/register")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("serialNumber")))
                .andReturn();
    }
}