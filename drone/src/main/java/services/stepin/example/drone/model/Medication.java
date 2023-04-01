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

        Matcher nameMatcher = NAME_PATTERN.matcher(medication.name);
        if(! nameMatcher.matches()){
            throw new InvalidMedicineException(" Name does not match the pattern: " + NAME_PATTERN);
        }

        Matcher codeMatcher = CODE_PATTERN.matcher(medication.code);
        if(! codeMatcher.matches()){
            throw new InvalidMedicineException(" Code does not match the pattern: " + CODE_PATTERN);
        }

    }
}
