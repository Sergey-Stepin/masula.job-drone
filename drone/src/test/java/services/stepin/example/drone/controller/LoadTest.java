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
import services.stepin.example.drone.controller.dto.MedicationDto;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Load;
import services.stepin.example.drone.model.Medication;
import services.stepin.example.drone.repository.DroneRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static services.stepin.example.drone.model.Drone.Model.HEAVYWEIGHT;

@SpringBootTest
@AutoConfigureMockMvc
class LoadTest {

    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DroneRepository droneRepository;

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
            droneRepository.save(drone);
        }

    }

    @Test
    void givenLoad_ShouldPersist() throws Exception {

        Load load = createSimpleLoad();

        LoadDto requestDto = LoadDto.toDto(load);

        MvcResult mvcResult = mockMvc.perform(post("/load")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String stingResponse = mvcResult.getResponse().getContentAsString();

        LoadDto responseDto = mapper.readValue(stingResponse, LoadDto.class);

        checkResponse(responseDto);
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
        medication1.setName("123456-oNPnsdf_1");
        medication1.setCode("01T");
        medication1.setWeightGram(20);
        medications.add(medication1);

        Medication medication2 = new Medication();
        medication2.setName("1RpoeDon-9098n-sdf_2");
        medication2.setCode("2_D");
        medication2.setWeightGram(50);
        medications.add(medication2);
        byte[] image2 = resourceHelper.getImage("med_10.jpeg");
        medication2.setImage(image2);

        return medications;
    }

    private void checkResponse(LoadDto loadDto){

        assertNotNull(loadDto);

        assertNotEquals(0, loadDto.getLoadId());
        assertNotEquals(0, loadDto.getDroneId());
        assertNotNull(loadDto.getCreatedAt());
        assertNull(loadDto.getLoadedAt());
        assertNull(loadDto.getUnloadedAt());

        List<MedicationDto> medications = loadDto.getMedications();
        assertEquals(2, medications.size());

        checkFirstMedication(medications);
        checkSecondMedication(medications);
    }

    private void checkFirstMedication(List<MedicationDto> medications){

        Optional<MedicationDto> optionalMedication = findMedicationInList(medications, "123456-oNPnsdf_1");
        assertTrue(optionalMedication.isPresent());

        MedicationDto medication = optionalMedication.get();
        assertEquals("01T", medication.getCode());
        assertEquals(20, medication.getWeightGram());

        assertNull(medication.getImage());

    }

    private void checkSecondMedication(List<MedicationDto> medications){

        Optional<MedicationDto> optionalMedication = findMedicationInList(medications, "1RpoeDon-9098n-sdf_2");
        assertTrue(optionalMedication.isPresent());

        MedicationDto medication = optionalMedication.get();
        assertEquals("2_D", medication.getCode());
        assertEquals(50, medication.getWeightGram());

        String image = resourceHelper.getImageAsString( "med_10.jpeg");
        assertEquals(image, medication.getImage(), "images mismatch");
    }

    private Optional<MedicationDto> findMedicationInList(List<MedicationDto> medications, String name) {
        return medications.stream()
                .filter(medication -> medication.getName().equals(name))
                .findAny();
    }
}