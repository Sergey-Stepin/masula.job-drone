package services.stepin.example.drone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import services.stepin.example.drone.ResourceHelper;
import services.stepin.example.drone.controller.dto.LoadDto;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Load;
import services.stepin.example.drone.model.Medication;
import services.stepin.example.drone.service.DroneService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static services.stepin.example.drone.model.Drone.Model.HEAVYWEIGHT;

@SpringBootTest
@AutoConfigureMockMvc
public class InvalidMedicineTest {

    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DroneService droneService;

    private final ResourceHelper resourceHelper = new ResourceHelper();

    private static Drone drone;

    @BeforeAll
    static void createDrone() {

        drone = new Drone();
        drone.setModel(HEAVYWEIGHT);
        drone.setSerialNumber("XYX-101");
        drone.setWeightLimitGram(480);
    }

    @BeforeEach
    void prepareDatabase(){

        if(drone.getDroneId() == 0){
            droneService.register(drone);
        }
    }

    @Test
    void givenInvalidName_ShouldThrow() throws Exception {
        Load load = new Load();
        load.setDrone(drone);

        List<Medication> medications = new ArrayList<>();
        Medication medication1 = new Medication();
        medication1.setName("___123AAS-456-ghuio4gdfg_1*");
        medication1.setCode("01T");
        medication1.setWeightGram(20);
        medications.add(medication1);
        load.setMedications(medications);

        load(load, "Name does not match the pattern");
    }

    @Test
    void givenInvalidCode_ShouldThrow() throws Exception {
        Load load = new Load();
        load.setDrone(drone);

        List<Medication> medications = new ArrayList<>();
        Medication medication1 = new Medication();
        medication1.setName("___123AAS-456-ghuio4gdfg_1");
        medication1.setCode("01Tx");
        medication1.setWeightGram(20);
        medications.add(medication1);
        load.setMedications(medications);

        load(load, "Code does not match the pattern");
    }

    private MvcResult load(Load load, String match) throws Exception {

        LoadDto requestDto = LoadDto.toDto(load);

        return mockMvc.perform(post("/load")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(content().string(containsString(match)))
                .andReturn();
    }

}
