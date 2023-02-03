package com.nighthawk.spring_portfolio.controllers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // annotation to create a RESTful web services
@RequestMapping("/api") // prefix of API

public class Races {

    private JSONObject body; // last run result
    private HttpStatus status; // last run status

    // GET schedule data
    @GetMapping("/races") // added to end of prefix as endpoint
    public ResponseEntity<JSONObject> getRaces(@RequestParam("year") String year) {
            try { // APIs can fail (ie Internet or Service down)

                // RapidAPI header
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(
                                "http://ergast.com/api/f1/" + year + "/races.json"))
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();

                // RapidAPI request and response
                HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                        HttpResponse.BodyHandlers.ofString());

                // JSONParser extracts text body and parses to JSONObject
                this.body = (JSONObject) new JSONParser().parse(response.body());
                this.status = HttpStatus.OK; // 200 success
            } catch (Exception e) { // capture failure info
                HashMap<String, String> status = new HashMap<>();
                status.put("status", "RapidApi failure: " + e);

                // Setup object for error
                this.body = (JSONObject) status;
                this.status = HttpStatus.INTERNAL_SERVER_ERROR; // 500 error
            }

        // return JSONObject in RESTful style
        return new ResponseEntity<>(body, status);
    }
}
