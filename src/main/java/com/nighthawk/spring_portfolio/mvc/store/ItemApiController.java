package com.nighthawk.spring_portfolio.mvc.store;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
            @RequestParam("cost") double cost, 
            @RequestParam("weight") double weight) {

        Item item = new Item(partType, description, cost, weight);
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

