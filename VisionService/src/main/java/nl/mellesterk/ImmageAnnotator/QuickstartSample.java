package nl.mellesterk.ImmageAnnotator;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import com.google.rpc.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class QuickstartSample {

  @Autowired
  private Environment environment;

  public static void main(String... args) throws Exception {
    String fileName = getFileNameFromArgs(args);
    if (fileName == null) {
      System.out.println("Please provide a non-empty --filename argument.");
      return;
    } else if (fileName.isEmpty() || !Paths.get(fileName).toFile().exists()) {
      System.out.println("Please provide a valid --filename argument. Example: --filename /path/to/image.jpg");
      return;
    }
    QuickstartSample app = new QuickstartSample();
    TextResult result = app.detectText(fileName, Function.identity());
    if (result.status instanceof com.google.rpc.Status) {
      System.out.println("Error: " + result.status.getMessage());
    } else {
      System.out.println("Result: " + result.result);
    }
  }

  private static String getFileNameFromArgs(String[] args) {
    String fileName = null;
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--filename") && i + 1 < args.length) {
        fileName = args[i + 1];
        break;
      }
    }
    return fileName;
  }

  public record TextResult(AnnotateImageResponse result, com.google.rpc.Status status) {
  }

  public TextResult detectText(String fileName, Function<TextResult, TextResult> postProcess) {
    // The path to the image file to annotate
    // https://cloud.google.com/static/vision/docs/images/sign_text.png
    if (fileName == null) {
      fileName = "/home/melle/VissionExample/VisionService/src/main/resources/static/img/pexels-nathan-poncet-14818915.jpg";
    }
    Path path = Paths.get(fileName);
    ByteString imgBytes;
    try {
      imgBytes = ByteString.readFrom(Files.newInputStream(path));
    } catch (IOException e) {
      System.out.println("Error reading file: " + e.getMessage());
      return new TextResult(null, Status.newBuilder().setMessage(e.toString()).build());
    }

    Image img = Image.newBuilder().setContent(imgBytes).build();
    return detectText(img, postProcess);
  }

  public TextResult detectText(Image img, Function<TextResult, TextResult> postProcess) {

    // Initialize client that will be used to send requests. This client only needs
    // to be created
    // once, and can be reused for multiple requests. After completing all of your
    // requests, call
    // the "close" method on the client to safely clean up any remaining background
    // resources.
    try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

      // ImageSource source =
      // ImageSource.newBuilder().setImageUri(inputImageUri).build();

      // Builds the image annotation request
      List<AnnotateImageRequest> requests = new ArrayList<>();
      List<Feature> feats = Arrays.asList(
          Feature.newBuilder().setType(Type.TEXT_DETECTION).build(),
          Feature.newBuilder().setType(Type.OBJECT_LOCALIZATION).setMaxResults(100).build());
      AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addAllFeatures(feats).setImage(img).build();
      requests.add(request);

      // Initialize client that will be used to send requests. This client only needs
      // to be created
      // once, and can be reused for multiple requests. After completing all of your
      // requests, call
      // the "close" method on the client to safely clean up any remaining background
      // resources.
      try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
        BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = response.getResponsesList();

        for (AnnotateImageResponse res : responses) {

          if (res.hasError()) {
            System.out.format("Error: %s%n", res.getError().getMessage());
            return new TextResult(null, res.getError());
          }
          Gson gson = new GsonBuilder().setPrettyPrinting().create();
          String json = gson.toJson(res);
          saveToDisk("./json.json", json);
          return postProcess.apply(new TextResult(res, null));
         
        }
      }
    } catch (Exception e) {
      System.out.println("Error during detectText: \n" + e.toString());
      return new TextResult(null, Status.newBuilder().setMessage(e.toString()).build());
    }
    return new TextResult(null, Status.newBuilder().setMessage("Unknown error nothin returned").build());
  }

  void saveToDisk(String fileName, String message) {

    if (environment != null) {
      System.out.println(environment.getActiveProfiles());
      String path = environment.getProperty("outputPath");
      if (path != null) {
        fileName = path + "/" + fileName;
      }
    }
    Path outputPath = Paths.get(fileName);
    try {
      Files.write(outputPath, message.getBytes());
      System.out.println("JSON saved to file: " + outputPath);
    } catch (IOException e) {
      System.out.println("Error saving JSON to file: " + e.getMessage());
    }
  }
}