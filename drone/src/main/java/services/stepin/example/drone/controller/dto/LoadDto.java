package services.stepin.example.drone.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import services.stepin.example.drone.model.Load;
import services.stepin.example.drone.model.Medication;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoadDto {

    private long loadId;

    private long droneId;

    private OffsetDateTime createdAt;

    private OffsetDateTime loadedAt;

    private OffsetDateTime unloadedAt;

    private List<MedicationDto> medications;

    public static LoadDto toDto(Load load){

        if(load == null){
            return null;
        }

        List< MedicationDto> medicationDtos = createMedicationDtoList(load);

        return LoadDto.builder()
                .loadId(load.getLoadId())
                .droneId(load.getDrone().getDroneId())
                .createdAt(load.getCreatedAt())
                .loadedAt(load.getLoadedAt())
                .unloadedAt(load.getUnloadedAt())
                .medications(medicationDtos)
                .build();
    }

    public static Load fromDto(LoadDto dto){

        if(dto == null){
            return null;
        }

        List< Medication> medications = createMedicationList(dto);

        Load load = new Load();
        load.setLoadId(dto.loadId);
        load.setCreatedAt(dto.createdAt);
        load.setLoadedAt(dto.loadedAt);
        load.setUnloadedAt(dto.unloadedAt);
        load.setMedications(medications);

        return load;
    }

    private static List<MedicationDto> createMedicationDtoList(Load load){

        if(load == null || load.getMedications() == null){
            return Collections.emptyList();
        }

        return load.getMedications()
                .stream()
                .map(MedicationDto::toDto)
                .toList();
    }

    private static List<Medication> createMedicationList(LoadDto loadDto){

        if(loadDto == null || loadDto.getMedications() == null){
            return Collections.emptyList();
        }

        return loadDto.getMedications()
                .stream()
                .map(MedicationDto::fromDto)
                .toList();
    }
}
