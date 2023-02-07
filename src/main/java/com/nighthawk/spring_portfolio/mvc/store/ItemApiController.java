package com.nighthawk.spring_portfolio.mvc.store;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // annotation to create a RESTful web services
@RequestMapping("/api/item") // prefix of API
public class ItemApiController {

    @Autowired
    private ItemJpaRepository itemJpaRepository;

    private JSONObject body; // last run result
    private HttpStatus status; // last run status

    // GET schedule data
    @PostMapping(value = "/newItem", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newTeam(@RequestParam("partType") String partType,
            @RequestParam("description") String description, 
            @RequestParam("currentCost") double currentCost,
            @RequestParam("initialCost") double initialCost, 
            @RequestParam("endDate") String endDateString, 
            @RequestParam("imageUrl") String imageUrl,
            @RequestParam("weight") double weight) {
                Date date;
                try {
                    date = new SimpleDateFormat("MM-dd-yyyy").parse(endDateString);
                } catch (Exception e) {
                    return new ResponseEntity<>(endDateString + " error; try MM-dd-yyyy",
                            HttpStatus.BAD_REQUEST);
                }
        Item item = new Item(partType, description, weight, date, currentCost, initialCost, imageUrl);
        itemJpaRepository.save(item);
        return new ResponseEntity<>(partType + "  has been successfully put up for sale!", HttpStatus.CREATED);
    }

    /*
     * GET List of Races
     */
    @GetMapping("/")
    public ResponseEntity<List<Item>> getItems() {
        return new ResponseEntity<>(itemJpaRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }
}

