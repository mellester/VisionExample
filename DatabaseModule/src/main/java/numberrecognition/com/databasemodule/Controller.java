package numberrecognition.com.databasemodule;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Validated
@RequestMapping("/api/test")
public class Controller {

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity GetRecord() {
        return ResponseEntity.ok("Hello world"
                );
    }
}
