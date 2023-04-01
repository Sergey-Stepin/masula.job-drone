package services.stepin.example.drone.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Medication {

    @Pattern(regexp = "[\\d\\w\\-\\_]*")
    private String name;

    @NotNull
    @Positive
    private int weightGram;

    @NotNull
    @Pattern(regexp = "[A-Z|\\_|\\d]*")
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
}
