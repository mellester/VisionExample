package nl.mellesterk.ImmageAnnotator;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.web.multipart.MultipartFile;


public class ImageMetaDataReader {
public record ImageMetaData(int width, int height) {
}
    public static ImageMetaData Read( MultipartFile file) {
        
        try {
            
            BufferedImage image = ImageIO.read(file.getInputStream());
            
            int width = image.getWidth();
            int height = image.getHeight();
            
            System.out.println("Image Width: " + width);
            System.out.println("Image Height: " + height);
            return new ImageMetaData(width, height);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not read image metadata");
        }

    }
}
