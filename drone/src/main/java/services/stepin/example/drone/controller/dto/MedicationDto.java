package services.stepin.example.drone.controller.dto;

import lombok.*;
import services.stepin.example.drone.model.Medication;

import java.util.Base64;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDto {

    private long medicationId;

    private String name;

    private long loadId;

    private int weightGram;

    private String code;

    private String image;

    @Override
    public String toString() {
        return "MedicationDto{" +
                "medicationId=" + medicationId +
                ", name='" + name + '\'' +
                ", loadId=" + loadId +
                ", weightGram=" + weightGram +
                ", code='" + code + '\'' +
                ", image_length='" + (image != null ? image.length() : 0) + '\'' +
                '}';
    }

    public static MedicationDto toDto(Medication medication){

        String imageAsString = convertImage(medication.getImage());

        return MedicationDto.builder()
                .medicationId(builder().medicationId)
                .name(medication.getName())
                .weightGram(medication.getWeightGram())
                .code(medication.getCode())
                .image(imageAsString)
                .build();
    }

    public static Medication fromDto(MedicationDto dto){

        byte[] image = convertImage(dto.getImage());

        Medication medication = new Medication();
        medication.setCode(dto.code);
        medication.setName(dto.name);
        medication.setWeightGram(dto.weightGram);
        medication.setImage(image);

        return medication;
    }

    private static String convertImage(byte[] image){

        if(image == null){
            return null;
        }

        return Base64.getEncoder().encodeToString(image);
    }

    private static byte[] convertImage(String imageAsString){

        if(imageAsString == null){
            return null;
        }

        return Base64.getDecoder().decode(imageAsString);
    }
}
