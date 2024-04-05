package nl.mellesterk.ImmageAnnotator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Image;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;

import nl.mellesterk.ImmageAnnotator.ImageMetaDataReader.ImageMetaData;
import nl.mellesterk.ImmageAnnotator.QuickstartSample.TextResult;

import java.util.function.Function;
import java.util.logging.Logger;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@Controller
public class VisionController {

  private static final Logger logger = Logger.getLogger(VisionController.class.getName());

  private final Bucket bucket;

  public VisionController() {
      Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofHours(1)));
      this.bucket = Bucket.builder()
          .addLimit(limit)
          .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofSeconds(20))))
          .build();
  }

  @Autowired
  private SpringTemplateEngine springTemplateEngine;

  @Autowired
  private QuickstartSample quickstartSample;

  @Autowired
  private ImageController imageController;

  @GetMapping
  public String index() {
    return "index";
  }

  private Function<TextResult, TextResult> postProcesFunctor(ImageMetaData size) {
    return (TextResult result) -> {
      if (result.status() instanceof com.google.rpc.Status) {
        return result;
      }
      result.result().getLocalizedObjectAnnotationsList().forEach(annotation -> {
        // if ("License plate".equals(annotation.getName())) {
          System.out.println("Object name: " + annotation.getName());
          System.out.println("Object score: " + annotation.getScore());
          annotation.getBoundingPoly().getNormalizedVerticesList().forEach(vertex -> {
            System.out.println("Vertex: " + vertex.getX() * size.width() + ", " + vertex.getY() * size.height());
          });
        // }
      });
      return result;
    };
  }

  @PostMapping("/post")
  public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("imgLink") String imgLink, 
      @RequestHeader("Hx-Request") String HxRequest) {
    logger.info("File upload called with file: " + file.getOriginalFilename());
    String message = "";
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    TextResult result = null;
    try {
      var size = ImageMetaDataReader.Read(file);
      if (checkFileExist("./" + file.getOriginalFilename() + ".json")) {
        logger.info("File already exists");
        String json = new String(Files.readAllBytes(Paths.get("./" + file.getOriginalFilename() + ".json")));
        result = new TextResult(gson.fromJson(json, AnnotateImageResponse.class), null);
        result = this.postProcesFunctor(size).apply(result);
      } else {

        var imgBytes = ByteString.readFrom(file.getInputStream());
        Image img = Image.newBuilder().setContent(imgBytes).build();

        result = quickstartSample.detectText(img, this.postProcesFunctor(size));
        if (result.status() instanceof com.google.rpc.Status) {
          message = "Error: " + result.status().getMessage();
          return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
        var res = result.result();
        try {
          Files.write(Paths.get("./" + file.getOriginalFilename() + ".json"), gson.toJson(res).getBytes());
        } catch (IOException ioException) {
          // TODO: handle exception
        }
      }
      var res = result.result();
      ResponseEntity<String> response = null;
      if ("true".equals(HxRequest.trim().toLowerCase())) {
        response =  imageController.generateImageAnnotations(res, size, imgLink);
      } else {
        message = gson.toJson(res);
      }

      try {
        Files.write(Paths.get("./out.txt"), message.getBytes());
      } catch (IOException ioException) {
        // TODO: handle exception
      }

      return response;
    } catch (Exception e) {
      message = "FAIL to upload " + file.getOriginalFilename() + "!";

      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
    }
  }

  public static boolean checkFileExist(String filename) {
    return Files.exists(Paths.get(filename));
  }

}
