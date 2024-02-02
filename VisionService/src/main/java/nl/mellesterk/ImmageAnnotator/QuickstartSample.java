package nl.mellesterk.ImmageAnnotator;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import com.google.rpc.Status;

import org.javatuples.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths; 
import java.util.ArrayList;
import java.util.List;

public class QuickstartSample {

  public static void main(String... args) throws Exception {
    String fileName = getFileNameFromArgs(args);
    if (fileName == null) {
      System.out.println("Please provide a non-empty --filename argument.");
      return;
    } else if (fileName.isEmpty() || !Paths.get(fileName).toFile().exists()) {
      System.out.println("Please provide a valid --filename argument. Example: --filename /path/to/image.jpg");
      return;
    }
    textResult result = detectText(fileName);
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

  public record textResult (AnnotateImageResponse result, com.google.rpc.Status status) {
  }
  public static textResult detectText(String fileName) {
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
        return new textResult(null, Status.newBuilder().setMessage(e.toString()).build());
      }
  
      Image img = Image.newBuilder().setContent(imgBytes).build();
      return detectText(img);
  }

  public static textResult detectText(Image img) {
  
    // Initialize client that will be used to send requests. This client only needs
    // to be created
    // once, and can be reused for multiple requests. After completing all of your
    // requests, call
    // the "close" method on the client to safely clean up any remaining background
    // resources.
    try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {



      // ImageSource source = ImageSource.newBuilder().setImageUri(inputImageUri).build();


      // Builds the image annotation request
      List<AnnotateImageRequest> requests = new ArrayList<>();
      Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
      AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
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
            return new textResult(null, res.getError());
          }
          // Gson gson = new GsonBuilder().setPrettyPrinting().create();
          // return new textResult(gson.toJson(res), null);
          return new textResult(res, null);
        }
      }
    } catch (Exception e) {
      System.out.println("Error during detectText: \n" + e.toString());
      return new textResult(null, Status.newBuilder().setMessage(e.toString()).build());
    }
    return new textResult(null, Status.newBuilder().setMessage("Unknown error nothin returned").build());
  }   
}