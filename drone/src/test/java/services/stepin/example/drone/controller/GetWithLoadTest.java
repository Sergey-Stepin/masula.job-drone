package services.stepin.example.drone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import services.stepin.example.drone.ResourceHelper;
import services.stepin.example.drone.controller.dto.DroneDto;
import services.stepin.example.drone.controller.dto.LoadDto;
import services.stepin.example.drone.controller.dto.MedicationDto;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.model.Load;
import services.stepin.example.drone.model.Medication;
import services.stepin.example.drone.service.DroneService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static services.stepin.example.drone.model.Drone.Model.*;

@SpringBootTest
@AutoConfigureMockMvc
class GetWithLoadTest {

    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DroneService droneService;

    private final ResourceHelper resourceHelper = new ResourceHelper();

    @Test
    void givenDrone_shouldGetWithLoad() throws Exception {

        Drone drone = createDrone();
        DroneDto registeredDroneDto = registerDrone(drone);

        long droneId = registeredDroneDto.getDroneId();

        checkDroneWithoutLoad(droneId);

        drone.setDroneId(droneId);
        Load load = createValidLoad(drone);

        LoadDto loadDto = load(load);
        long loadId = loadDto.getLoadId();

        checkDroneWithLoad(droneId, loadId);

        droneService.unload(loadId);

        checkUnloaded(droneId, loadId);
    }

    private Drone createDrone(){

        Drone drone = new Drone();
        drone.setModel(LIGHTWEIGHT);
        drone.setSerialNumber("X");
        drone.setWeightLimitGram(50);

        return drone;
    }

    private Load createValidLoad(Drone drone) {

        Load load = new Load();
        load.setDrone(drone);

        List<Medication> medications = createValidMedicationList();
        load.setMedications(medications);

        return load;
    }

    private List<Medication> createValidMedicationList() {

        List<Medication> medications = new ArrayList<>();

        Medication medication1 = new Medication();
        medication1.setName("1");
        medication1.setCode("A");
        medication1.setWeightGram(10);
        medications.add(medication1);

        Medication medication2 = new Medication();
        medication2.setName("2");
        medication2.setCode("B");
        medication2.setWeightGram(20);
        medications.add(medication2);
        byte[] image2 = resourceHelper.getImage("med_20.avif");
        medication2.setImage(image2);

        return medications;
    }

    private DroneDto registerDrone(Drone drone) throws Exception{
        DroneDto droneDto = DroneDto.toDto(drone);

        MvcResult mvcResult = mockMvc.perform(post("/register")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(droneDto)))
                .andExpect(status().isOk())
                .andReturn();

        return getResponse(mvcResult, DroneDto.class);
    }

    private LoadDto load(Load load) throws Exception {

        LoadDto loadDto = LoadDto.toDto(load);

        MvcResult mvcResult = mockMvc.perform(post("/load")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(loadDto)))
                .andExpect(status().isOk())
                .andReturn();

        return getResponse(mvcResult, LoadDto.class);

    }

    private void checkDroneWithoutLoad(long droneId) throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/drone_with_load?droneId=" + droneId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        DroneDto droneDto = getResponse(mvcResult, DroneDto.class);

        assertNull(droneDto.getLoadDto());
    }

    private void checkDroneWithLoad(long droneId, long loadId) throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/drone_with_load?droneId=" + droneId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        DroneDto droneDto = getResponse(mvcResult, DroneDto.class);

        LoadDto loadDto = droneDto.getLoadDto();
        checkLoad(loadDto, loadId);
    }

    private void checkUnloaded(long droneId, long loadId) throws Exception {
        checkDroneWithoutLoad(droneId);
    }


    private void checkLoad(LoadDto loadDto, long loadId){

        assertNotNull(loadDto);

        assertEquals(loadDto.getLoadId(), loadId);

        assertNotNull(loadDto.getCreatedAt());
        assertNull(loadDto.getLoadedAt());
        assertNull(loadDto.getUnloadedAt());

        List<MedicationDto> medications = loadDto.getMedications();
        assertEquals(2, medications.size());

        checkFirstMedication(medications);
        checkSecondMedication(medications);
    }

    private void checkFirstMedication(List<MedicationDto> medications){

        Optional<MedicationDto> optionalMedication = findMedicationInList(medications, "1");
        assertTrue(optionalMedication.isPresent());

        MedicationDto medication = optionalMedication.get();
        assertEquals("A", medication.getCode());
        assertEquals(10, medication.getWeightGram());

        assertNull(medication.getImage());

    }

    private void checkSecondMedication(List<MedicationDto> medications){

        Optional<MedicationDto> optionalMedication = findMedicationInList(medications, "2");
        assertTrue(optionalMedication.isPresent());

        MedicationDto medication = optionalMedication.get();
        assertEquals("B", medication.getCode());
        assertEquals(20, medication.getWeightGram());

        String image = resourceHelper.getImageAsString( "med_20.avif");
        assertEquals(image, medication.getImage(), "images mismatch");
    }


    private <T> T getResponse(MvcResult mvcResult, Class<T> clazz) throws UnsupportedEncodingException, JsonProcessingException {
        String stingResponse = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(stingResponse, clazz);
    }

    private Optional<MedicationDto> findMedicationInList(List<MedicationDto> medications, String name) {
        return medications.stream()
                .filter(medication -> medication.getName().equals(name))
                .findAny();
    }
}