package nl.mellesterk.ImmageAnnotator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.google.cloud.vision.v1.Image;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;



    import java.util.logging.Logger;

    @Controller
    public class VisionController {

      private static final Logger logger = Logger.getLogger(VisionController.class.getName());

      
@Autowired
private SpringTemplateEngine springTemplateEngine;


     @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping("/post")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestHeader("Hx-Request") String HxRequest) {
      logger.info("File upload called with file: " + file.getOriginalFilename());
      String message = "";
      try {
        var imgBytes = ByteString.readFrom(file.getInputStream());
        Image img = Image.newBuilder().setContent(imgBytes).build();
        var result = QuickstartSample.detectText(img);
        if (result.status() instanceof com.google.rpc.Status) {
          message = "Error: " + result.status().getMessage();
          return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
        var res = result.result();


        if ("true".equals(HxRequest.trim().toLowerCase())) {
          
          Context myContext = new Context();
          myContext.setVariable("strings", res.getTextAnnotationsList());
          message = springTemplateEngine.process("post", myContext);
        } else {
          Gson gson = new GsonBuilder().setPrettyPrinting().create();
          message = gson.toJson(res);
        }


        try {
          Files.write(Paths.get("./out.txt"), message.getBytes());
        } catch (IOException ioException) {
          // TODO: handle exception
        }

        return ResponseEntity.status(HttpStatus.OK).body(message);
      } catch (Exception e) { 
      message = "FAIL to upload " + file.getOriginalFilename() + "!";

      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
      }
  }

}
