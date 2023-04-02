package services.stepin.example.drone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import services.stepin.example.drone.controller.dto.LoadDto;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Load;
import services.stepin.example.drone.model.Medication;
import services.stepin.example.drone.service.DroneService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static services.stepin.example.drone.model.Drone.Model.LIGHTWEIGHT;

@SpringBootTest
@AutoConfigureMockMvc
public class InvalidLoadTest {

    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
    }


    @Autowired
    private DroneService droneService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenInvalidLoad_BadRequest() throws Exception {

        Drone drone = new Drone();
        drone.setModel(LIGHTWEIGHT);
        drone.setSerialNumber("-90io;lIHn---({J:Nrhr");
        drone.setWeightLimitGram(100);
        drone.setBatteryLevel(24);
        Drone persistedDrone = droneService.register(drone);

        Load load = new Load();
        load.setDrone(persistedDrone);

        List<Medication> medications = new ArrayList<>();

        Medication medication1 = new Medication();
        medication1.setName("80_9ik-f3pkmlkUHOJB");
        medication1.setCode("DJLK00--DD");
        medication1.setWeightGram(10);
        medications.add(medication1);
        load.setMedications(medications);

        LoadDto requestDto = LoadDto.toDto(load);

        mockMvc.perform(post("/load")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
