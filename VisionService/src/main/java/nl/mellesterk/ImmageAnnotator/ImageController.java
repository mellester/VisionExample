package nl.mellesterk.ImmageAnnotator;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.google.cloud.vision.v1.AnnotateImageResponse;

import nl.mellesterk.ImmageAnnotator.ImageMetaDataReader.ImageMetaData;

@Component
public class ImageController {

    private static final Logger logger = Logger.getLogger(ImageController.class.getName());

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    // @Autowired
    // private Environment environment;

    public ResponseEntity<String> generateImageAnnotations(AnnotateImageResponse result, ImageMetaData size,
            String imgLink) {
        Context processingContext = new Context();
        processingContext.setVariable("width", size.width());
        processingContext.setVariable("height", size.height());
        processingContext.setVariable("imgLink", imgLink);
        processingContext.setVariable("textPlates", getText(result));
        System.out.println("Text Plates: " + result.getTextAnnotationsList());
        processingContext.setVariable("plates", getVertices(result, size));
        String message = springTemplateEngine.process("post", processingContext);
        result.getTextAnnotationsList().forEach(annotation -> {
            logger.info(annotation.getDescription());
            logger.info("Position : " + annotation.getBoundingPoly());
        });
        return ResponseEntity.ok(message);

    }

    public ArrayList<ArrayList<Integer>> getVertices(AnnotateImageResponse result, ImageMetaData size) {
        ArrayList<ArrayList<Integer>> plates = new ArrayList<>();
        result.getLocalizedObjectAnnotationsList().forEach(annotation -> {
            ArrayList<Integer> vertices = new ArrayList<>();
            if ("License plate".equals(annotation.getName())) {
                annotation.getBoundingPoly().getNormalizedVerticesList().forEach(vertex -> {
                    vertices.add((int) (vertex.getX() * size.width()));
                    vertices.add((int) (vertex.getY() * size.height()));
                });
                plates.add(vertices);
            }
        });
        return plates;
    }

    public Set<String> getText(AnnotateImageResponse result) {
        Set<String> plates = new TreeSet<>();
        String regex = "([A-Z0-9]){1,3}-([A-Z0-9]){2,3}-([A-Z0-9]){1,3}";
        Pattern pattern = Pattern.compile(regex);
        result.getTextAnnotationsList().forEach(annotation -> {
            String input = annotation.getDescription();
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                System.out.println("Match found: " + matcher.group() + " at index " + matcher.start());
                plates.add(matcher.group());
            }

        });
        return plates;
    }

}
