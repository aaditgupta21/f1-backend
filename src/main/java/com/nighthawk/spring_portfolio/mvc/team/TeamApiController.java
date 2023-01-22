package com.nighthawk.spring_portfolio.mvc.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nighthawk.spring_portfolio.mvc.user.User;

import java.util.*;

import javax.transaction.Transactional;

import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/auth")
public class TeamApiController {
    // Autowired enables Control to connect POJO Object through JPA
    @Autowired
    private TeamJpaRepository repository;

    // TODO: needs security access since we only want admins to create a new team
    @PostMapping(value = "/newTeam", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newTeam(@RequestParam("name") String name,
            @RequestParam("location") String location) {

        Team team = new Team(name, location);
        repository.save(team);
        return new ResponseEntity<>(name + " team has been successfully created", HttpStatus.CREATED);
    }

    @PostMapping(value = "/newUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newUser(@RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            @RequestParam("dob") String dobString,
            @RequestParam("gender") char gender,
            @RequestParam("teamID") long teamID) {

        // Create DOB
        Date dob;

        try {
            dob = new SimpleDateFormat("MM-dd-yyyy").parse(dobString);
        } catch (Exception e) {
            return new ResponseEntity<>(dobString + " error; try MM-dd-yyyy",
                    HttpStatus.BAD_REQUEST);
        }

        // find team by ID
        Optional<Team> optional = repository.findById(teamID);
        if (optional.isPresent()) { // Good ID
            Team team = optional.get(); // value from findByID
            User user = new User(email, password, gender, name, dob);

            team.getUsers().add(user);
            // repository.save(user); // conclude by writing the user updates

            // return user (or return w message of successfully created user)
            return new ResponseEntity<>(user, HttpStatus.OK);
        }

        // return Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // Date dob;
        // try {
        // dob = new SimpleDateFormat("MM-dd-yyyy").parse(dobString);
        // } catch (Exception e) {
        // return new ResponseEntity<>(dobString + " error; try MM-dd-yyyy",
        // HttpStatus.BAD_REQUEST);
        // }
        // A person object WITHOUT ID will create a new record with default roles as
        // student
        // User user = new User(null, email, password, gender, name, dob);
        // Optional<Team> teamOptional = repository.findById(id);

        // if (optional.isPresent()) {
        // user.setTeam(teamOptional.get());
        // } else {
        // return new ResponseEntity<>(team + " not found", HttpStatus.BAD_REQUEST);
        // }
        // repository.save(user);
        // return new ResponseEntity<>(email + " is created successfully",
        // HttpStatus.CREATED);
    }
}
