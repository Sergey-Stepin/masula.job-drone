package services.stepin.example.drone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import services.stepin.example.drone.controller.dto.DroneDto;
import services.stepin.example.drone.model.Drone;
import services.stepin.example.drone.service.DroneCommunicator;
import services.stepin.example.drone.service.DroneMonitor;
import services.stepin.example.drone.service.DroneService;
import services.stepin.example.drone.service.impl.DroneMonitorImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static services.stepin.example.drone.model.Drone.Model.*;

@SpringBootTest
@AutoConfigureMockMvc
class BatteryLeveTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DroneService droneService;

    @Mock
    private DroneCommunicator droneCommunicator;

    private DroneMonitor droneMonitor;

    @BeforeEach
    public void createDroneMonitor(){
        droneMonitor = new DroneMonitorImpl(droneCommunicator, droneService);
    }

    @Test
    void givenDrones_shouldGetBatteryLevel() throws Exception {

        Drone drone = createAndRegisterDrone(100);
        assertEquals(100, getBatteryLevel(drone));

        when(droneCommunicator.getBatteryLevel(drone)).thenReturn(75);
        droneMonitor.monitor(drone);
        assertEquals(75, getBatteryLevel(drone));

        Drone another = createAndRegisterDrone(30);
        assertEquals(30, getBatteryLevel(another));

        when(droneCommunicator.getBatteryLevel(drone)).thenReturn(25);
        droneMonitor.monitor(drone);
        assertEquals(25, getBatteryLevel(drone));
    }

    private Drone createAndRegisterDrone(int batteryLevel) throws Exception {

        Drone drone = createDrone(batteryLevel);
        DroneDto droneDto = register(drone);

        drone.setDroneId(droneDto.getDroneId());

        return drone;
    }

    private Drone createDrone(int batteryLevel){
        Drone drone = new Drone();
        drone.setModel(LIGHTWEIGHT);
        drone.setSerialNumber("_xyz-13");
        drone.setWeightLimitGram(66);
        drone.setBatteryLevel(batteryLevel);
        drone.setBatteryLevel(batteryLevel);
        return drone;
    }

    private DroneDto register(Drone drone) throws Exception {

        DroneDto requestDto = DroneDto.toDto(drone);

        MvcResult mvcResult = mockMvc.perform(post("/register")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String stingResponse = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(stingResponse, DroneDto.class);
    }

    private int getBatteryLevel(Drone drone) throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/battery_level?droneId=" + drone.getDroneId())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String stingResponse = mvcResult.getResponse().getContentAsString();
        return mapper.readValue(stingResponse, Integer.class);
    }


}