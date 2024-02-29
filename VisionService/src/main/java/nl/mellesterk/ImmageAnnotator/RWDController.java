package nl.mellesterk.ImmageAnnotator;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Validated
@RestController
public class RWDController {

    @GetMapping("/rwd")
    public ResponseEntity<String> getRWDData(@RequestParam
    @Pattern(regexp = "^[A-Z0-9-]{6,9}$", message = "Invalid Kenteken format") 
     String Kenteken
     ) throws Exception, MethodArgumentNotValidException {
        String url = "https://ovi.rdw.nl/default.aspx"; // Insert your URL here
        String base = "https://ovi.rdw.nl/";
        String postData = "__VIEWSTATE=%2FwEPDwUKMTE1NDI3MDEyOQ9kFgJmD2QWAgIDD2QWAgIBD2QWAgIJDxYCHgdWaXNpYmxlaGRkbswJ9j6e1dGi3igmthIyvj8gc4WH7JTnjgF14yLn50Y%3D&__VIEWSTATEGENERATOR=CA0B0334&__EVENTVALIDATION=%2FwEdAAK8JKqL%2BRK4NWrMB2xu8rTB851Fq81QBiZgFEttEk2eeHewUOoCJ2ceZf9ZQoBBMXI2OTBKyEO6GXbPjhMnQTm0&ctl00%24TopContent%24txtKenteken=";
        postData += Kenteken;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Set request method
        con.setRequestMethod("POST");

        // Set request headers
        con.setRequestProperty("sec-ch-ua", "\"Not A(Brand\";v=\"99\", \"Google Chrome\";v=\"121\", \"Chromium\";v=\"121\"");
        con.setRequestProperty("Accept", "text/html, */*; q=0.01");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        con.setRequestProperty("Referer", "https://ovi.rdw.nl/default.aspx");
        con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        con.setRequestProperty("sec-ch-ua-mobile", "?0");
        con.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");

        // Set request body
        con.setDoOutput(true);
        byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);
        con.getOutputStream().write(postDataBytes);

        // Send the request
        int responseCode = con.getResponseCode();

        // Read the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String regex = "\\s+src=\\\"((?!https)[^\\\"]+)\\\"";
        String replacement = " src=\"" + base + "$1\"";
        String modifiedResponse = response.toString().replaceAll(regex, replacement);

        regex = "\\s+href=\\\"((?!https)[^\\\"]+)\\\"";
        replacement = " href=\"" + base + "$1\"";
        modifiedResponse = modifiedResponse.replaceAll(regex, replacement);

        ResponseEntity<String> responseEntity = ResponseEntity.status(responseCode).body(modifiedResponse);


        return responseEntity;
    }
}