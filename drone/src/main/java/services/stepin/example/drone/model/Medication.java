package services.stepin.example.drone.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import services.stepin.example.drone.service.exception.InvalidMedicineException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Embeddable
@Getter
@Setter
public class Medication {

    private final static Pattern NAME_PATTERN = Pattern.compile("[\\w|\\d|\\-|\\_]+");
    private final static Pattern CODE_PATTERN = Pattern.compile("[A-Z|\\d||\\_]+");

    @NotNull
    private String name;

    @NotNull
    @Positive
    private int weightGram;

    @NotNull
    private String code;

    @Lob
    private byte[] image;

    @Override
    public String toString() {
        return "Medication{" +
                "name='" + name + '\'' +
                ", weightGram=" + weightGram +
                ", code='" + code + '\'' +
                ", image_length='" + (image != null ? image.length : 0) + '\'' +
                '}';
    }

    public static void validate(Medication medication){
        validateName(medication);
        validateCode(medication);
        validateWeight(medication);
    }

    private static void validateName(Medication medication){

        if(medication.name == null || medication.name.isBlank()){
            throw  new InvalidMedicineException(" name is either null or blank: " + medication.name);
        }

        Matcher nameMatcher = NAME_PATTERN.matcher(medication.name);
        if(! nameMatcher.matches()){
            throw new InvalidMedicineException(" Name does not match the pattern: " + NAME_PATTERN);
        }

    }

    private static void validateCode(Medication medication){

        if(medication.code == null || medication.code.isBlank()){
            throw  new InvalidMedicineException(" code is either null or blank: " + medication.code);
        }

        Matcher codeMatcher = CODE_PATTERN.matcher(medication.code);
        if(! codeMatcher.matches()){
            throw new InvalidMedicineException(" Code does not match the pattern: " + CODE_PATTERN);
        }

    }

    private static void validateWeight(Medication medication){

        if(medication.weightGram <= 0){
            throw  new InvalidMedicineException(" weightGram must be grater zero : " + medication.code);
        }

    }
}
