package services.stepin.example.drone;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

public class ResourceHelper {

    private final static String IMAGE_PATH = "images/";

    public String getImageAsString(String fileName){

        byte[] image = getImage(fileName);

        if(image == null){
            return null;
        }

        return Base64.getEncoder().encodeToString(image);
    }

    public byte[] getImage(String fileName){
        return getResourceAsByteArray(IMAGE_PATH + fileName);
    }

    public byte[] getResourceAsByteArray(String fileName) {

        try {

            File file = getFile(fileName);
            return Files.readAllBytes(file.toPath());

        } catch (Exception ex) {
            throw new RuntimeException(String.format(" Cannot read image %s : %s", fileName, ex.getMessage()), ex);
        }

    }

    public File getFile(String fileName) throws URISyntaxException {

        URL resource = this.getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        }

        return new File(resource.toURI());
    }
}
